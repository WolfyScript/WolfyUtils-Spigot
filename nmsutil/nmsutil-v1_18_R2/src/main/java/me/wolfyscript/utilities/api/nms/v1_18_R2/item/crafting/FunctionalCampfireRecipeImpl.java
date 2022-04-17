package me.wolfyscript.utilities.api.nms.v1_18_R2.item.crafting;

import me.wolfyscript.utilities.api.nms.inventory.FunctionalCampfireRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FunctionalCampfireRecipeImpl extends CampfireCookingRecipe implements FunctionalCampfireRecipe {

    private final BiFunction<Inventory, World, Boolean> matcher;
    private Function<Inventory, Optional<org.bukkit.inventory.ItemStack>> assembler;
    private Function<Inventory, Optional<List<org.bukkit.inventory.ItemStack>>> remainingItems;

    public FunctionalCampfireRecipeImpl(ResourceLocation key, String group, Ingredient ingredient, ItemStack result, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> matcher) {
        super(key, group, ingredient, result, experience, cookingTime);
        this.matcher = matcher;
        this.assembler = inventory -> Optional.empty();
        this.remainingItems = inventory -> Optional.empty();
    }

    @Override
    public boolean matches(Container container, Level level) {
        return matches(new CraftInventory(container), level.getWorld());
    }

    @Override
    public ItemStack assemble(Container container) {
        return assemble(new CraftInventory(container)).map(CraftItemStack::asNMSCopy).orElseGet(() -> super.assemble(container));
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(Container container) {
        return getRemainingItems(new CraftInventory(container)).map(itemStacks -> {
            NonNullList<ItemStack> items = NonNullList.createWithCapacity(itemStacks.size());
            for (int i = 0; i < itemStacks.size(); i++) {
                items.set(i, CraftItemStack.asNMSCopy(itemStacks.get(i)));
            }
            return items;
        }).orElseGet(()-> super.getRemainingItems(container));
    }

    @Override
    public BiFunction<Inventory, World, Boolean> getMatcher() {
        return matcher;
    }

    @Override
    public Function<Inventory, Optional<org.bukkit.inventory.ItemStack>> getAssembler() {
        return assembler;
    }

    @Override
    public void setAssembler(Function<Inventory, Optional<org.bukkit.inventory.ItemStack>> assembler) {
        this.assembler = assembler;
    }

    @Override
    public Function<Inventory, Optional<List<org.bukkit.inventory.ItemStack>>> getRemainingItemsFunction() {
        return remainingItems;
    }

    @Override
    public void setRemainingItemsFunction(Function<Inventory, Optional<List<org.bukkit.inventory.ItemStack>>> remainingItems) {
        this.remainingItems = remainingItems;
    }
}
