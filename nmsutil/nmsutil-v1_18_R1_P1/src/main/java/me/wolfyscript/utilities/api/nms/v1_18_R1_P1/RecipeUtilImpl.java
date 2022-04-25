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

package me.wolfyscript.utilities.api.nms.v1_18_R1_P1;

import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.item.crafting.FunctionalBlastingRecipe;
import me.wolfyscript.utilities.api.nms.item.crafting.FunctionalCampfireRecipe;
import me.wolfyscript.utilities.api.nms.item.crafting.FunctionalFurnaceRecipe;
import me.wolfyscript.utilities.api.nms.item.crafting.FunctionalRecipe;
import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
import me.wolfyscript.utilities.api.nms.item.crafting.FunctionalSmokingRecipe;
import me.wolfyscript.utilities.api.nms.v1_18_R1_P1.inventory.RecipeIterator;
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
