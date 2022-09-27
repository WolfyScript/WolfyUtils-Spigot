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

import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.BiFunction;

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

//    /**
//     * Creates a functional Campfire Recipe.<br>
//     * The recipeMatch function is used to check if the recipe is valid (Careful! This is called each tick!)<br>
//     * Other functions like assembler and remaining items, can be set later on.<br>
//     *
//     *
//     * @param key The id of the recipe.
//     * @param group The group of the recipe.
//     * @param result The result of the recipe. When using a custom assembler it is used as the display item.
//     * @param source Used to display the source item/s.
//     * @param experience The experience of the recipe.
//     * @param cookingTime The cooking time of the recipe.
//     * @param recipeMatch The function that checks if the recipe is valid.
//     * @return A new instance of the functional campfire recipe.
//     */
//    public abstract FunctionalCampfireRecipe campfireRecipe(NamespacedKey key, String group, ItemStack result, RecipeChoice source, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> recipeMatch);
//

}
