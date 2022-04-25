package me.wolfyscript.utilities.api.nms.v1_18_R2.item.crafting;

import me.wolfyscript.utilities.api.nms.item.crafting.FunctionalSmokingRecipe;
import me.wolfyscript.utilities.api.nms.v1_18_R2.NamespacedKeyUtils;
import me.wolfyscript.utilities.util.NamespacedKey;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FunctionalSmokingRecipeImpl extends SmokingRecipe implements FunctionalSmokingRecipe, FunctionalRecipeInternals {

    private final NamespacedKey recipeID;
    private final BiFunction<Inventory, World, Boolean> matcher;
    private Function<Inventory, Optional<org.bukkit.inventory.ItemStack>> assembler;
    private Function<Inventory, Optional<List<org.bukkit.inventory.ItemStack>>> remainingItems;

    public FunctionalSmokingRecipeImpl(NamespacedKey recipeID, String group, Ingredient ingredient, ItemStack result, float experience, int cookTime, BiFunction<Inventory, World, Boolean> matcher) {
        super(NamespacedKeyUtils.toMC(recipeID), group, ingredient, result, experience, cookTime);
        this.recipeID = recipeID;
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
        return assembleResult(container).orElseGet(() -> super.assemble(container));
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(Container container) {
        return calcRemainingItems(container).orElseGet(()-> super.getRemainingItems(container));
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

    @Override
    public NamespacedKey getNamespacedKey() {
        return recipeID;
    }
}
