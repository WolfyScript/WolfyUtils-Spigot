package me.wolfyscript.utilities.api.nms.v1_18_R2.item.crafting;

import me.wolfyscript.utilities.api.nms.inventory.FunctionalFurnaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FunctionalFurnaceRecipeImpl extends SmeltingRecipe implements FunctionalFurnaceRecipe {

    public FunctionalFurnaceRecipeImpl(ResourceLocation key, String group, Ingredient ingredient, ItemStack result, float experience, int cookTime, BiFunction<Inventory, World, Boolean> matcher) {
        super(key, group, ingredient, result, experience, cookTime);
    }

    @Override
    public BiFunction<Inventory, World, Boolean> getMatcher() {
        return null;
    }

    @Override
    public Function<Inventory, Optional<org.bukkit.inventory.ItemStack>> getAssembler() {
        return null;
    }

    @Override
    public void setAssembler(Function<Inventory, Optional<org.bukkit.inventory.ItemStack>> assembler) {

    }

    @Override
    public Function<Inventory, Optional<List<org.bukkit.inventory.ItemStack>>> getRemainingItemsFunction() {
        return null;
    }

    @Override
    public void setRemainingItemsFunction(Function<Inventory, Optional<List<org.bukkit.inventory.ItemStack>>> function) {

    }
}
