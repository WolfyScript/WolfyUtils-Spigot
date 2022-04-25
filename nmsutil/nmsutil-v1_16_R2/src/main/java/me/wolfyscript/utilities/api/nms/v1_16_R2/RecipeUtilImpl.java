package me.wolfyscript.utilities.api.nms.v1_16_R2;

import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.item.crafting.FunctionalBlastingRecipe;
import me.wolfyscript.utilities.api.nms.item.crafting.FunctionalCampfireRecipe;
import me.wolfyscript.utilities.api.nms.item.crafting.FunctionalFurnaceRecipe;
import me.wolfyscript.utilities.api.nms.item.crafting.FunctionalRecipe;
import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
import me.wolfyscript.utilities.api.nms.item.crafting.FunctionalSmokingRecipe;
import me.wolfyscript.utilities.api.nms.v1_16_R2.inventory.RecipeIterator;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.BiFunction;

public class RecipeUtilImpl extends me.wolfyscript.utilities.api.nms.RecipeUtil {

    protected RecipeUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public @NotNull Iterator<Recipe> recipeIterator(RecipeType recipeType) {
        return new RecipeIterator(recipeType);
    }

    @Override
    public FunctionalFurnaceRecipe furnaceRecipe(NamespacedKey key, String group, ItemStack result, RecipeChoice source, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> recipeMatch) {
        return null;
    }

    @Override
    public FunctionalCampfireRecipe campfireRecipe(NamespacedKey key, String group, ItemStack result, RecipeChoice source, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> recipeMatch) {
        return null;
    }

    @Override
    public FunctionalBlastingRecipe blastingRecipe(NamespacedKey key, String group, ItemStack result, RecipeChoice source, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> recipeMatch) {
        return null;
    }

    @Override
    public FunctionalSmokingRecipe smokingRecipe(NamespacedKey key, String group, ItemStack result, RecipeChoice source, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> recipeMatch) {
        return null;
    }

    @Override
    public void registerCookingRecipe(FunctionalRecipe recipe) {

    }

}
