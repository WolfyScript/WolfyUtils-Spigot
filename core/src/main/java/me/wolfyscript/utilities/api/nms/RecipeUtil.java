/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.api.nms;

import me.wolfyscript.utilities.api.nms.inventory.FunctionalBlastingRecipe;
import me.wolfyscript.utilities.api.nms.inventory.FunctionalCampfireRecipe;
import me.wolfyscript.utilities.api.nms.inventory.FunctionalCookingRecipe;
import me.wolfyscript.utilities.api.nms.inventory.FunctionalFurnaceRecipe;
import me.wolfyscript.utilities.api.nms.inventory.ExtendedRecipeChoice;
import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
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

public abstract class RecipeUtil extends UtilComponent {

    protected RecipeUtil(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    /**
     * Get the Iterator of the specific recipe type.
     * Other than the Bukkit Recipe Iterator this Iterator only contains the recipes of the specified type.
     *
     * @param recipeType The recipe type to get the iterator for.
     * @return The iterator of the recipe type.
     */
    public abstract @NotNull Iterator<Recipe> recipeIterator(RecipeType recipeType);

    public abstract ExtendedRecipeChoice recipeChoice(Function<ItemStack, Boolean> sourceCheck, @NotNull List<ItemStack> choices);

    public abstract ExtendedRecipeChoice recipeChoice(Function<ItemStack, Boolean> sourceCheck, @NotNull ItemStack choice);

    public abstract ExtendedRecipeChoice recipeChoice(Function<ItemStack, Boolean> sourceCheck, @NotNull ItemStack... choices);

    public abstract FunctionalFurnaceRecipe furnaceRecipe(NamespacedKey key, ItemStack result, ItemStack source, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> recipeMatch);

    public abstract FunctionalCampfireRecipe campfireRecipe(NamespacedKey key, ItemStack result, ItemStack source, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> recipeMatch);

    public abstract FunctionalBlastingRecipe blastingRecipe(NamespacedKey key, ItemStack result, ItemStack source, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> recipeMatch);

    public abstract void registerCookingRecipe(FunctionalCookingRecipe recipe);

}
