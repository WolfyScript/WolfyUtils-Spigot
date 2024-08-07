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

package com.wolfyscript.utilities.bukkit.nms.fallback;

import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class FallbackRecipeUtilImpl extends me.wolfyscript.utilities.api.nms.RecipeUtil {

    protected FallbackRecipeUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public @NotNull Iterator<Recipe> recipeIterator(RecipeType recipeType) {
        return new RecipeIterator(recipeType);
    }

    @Override
    public void setCurrentRecipe(InventoryView view, NamespacedKey namespacedKey) { }

    @Override
    public void setCurrentRecipe(Inventory inventory, NamespacedKey namespacedKey) { }
}
