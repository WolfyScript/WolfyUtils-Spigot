package me.wolfyscript.utilities.api.nms.v1_17_R1;

import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.inventory.FunctionalBlastingRecipe;
import me.wolfyscript.utilities.api.nms.inventory.FunctionalCampfireRecipe;
import me.wolfyscript.utilities.api.nms.inventory.FunctionalCookingRecipe;
import me.wolfyscript.utilities.api.nms.inventory.FunctionalFurnaceRecipe;
import me.wolfyscript.utilities.api.nms.inventory.ExtendedRecipeChoice;
import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
import me.wolfyscript.utilities.api.nms.v1_17_R1.inventory.RecipeIterator;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RecipeUtilImpl extends me.wolfyscript.utilities.api.nms.RecipeUtil {

    protected RecipeUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public @NotNull Iterator<Recipe> recipeIterator(RecipeType recipeType) {
        return new RecipeIterator(recipeType);
    }

    @Override
    public ExtendedRecipeChoice recipeChoice(Function<ItemStack, Boolean> sourceCheck, @NotNull List<ItemStack> choices) {
        return null;
    }

    @Override
    public ExtendedRecipeChoice recipeChoice(Function<ItemStack, Boolean> sourceCheck, @NotNull ItemStack choice) {
        return null;
    }

    @Override
    public ExtendedRecipeChoice recipeChoice(Function<ItemStack, Boolean> sourceCheck, @NotNull ItemStack... choices) {
        return null;
    }

    @Override
    public FunctionalFurnaceRecipe furnaceRecipe(NamespacedKey key, ItemStack result, ItemStack source, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> recipeMatch) {
        return null;
    }

    @Override
    public FunctionalCampfireRecipe campfireRecipe(NamespacedKey key, ItemStack result, ItemStack source, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> recipeMatch) {
        return null;
    }

    @Override
    public FunctionalBlastingRecipe blastingRecipe(NamespacedKey key, ItemStack result, ItemStack source, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> recipeMatch) {
        return null;
    }

    @Override
    public void registerCookingRecipe(FunctionalCookingRecipe recipe) {

    }


}
