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

import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
import org.bukkit.inventory.Recipe;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RecipeIterator implements Iterator<org.bukkit.inventory.Recipe> {

    private final Iterator<?> recipes; // List of RecipeHolder instances

    public RecipeIterator(RecipeType recipeType) {
        Iterator<?> iterator;
        try {
            iterator = getRecipesFor(recipeType).iterator();
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            iterator = Collections.emptyIterator();
        }
        this.recipes = iterator;
    }

    private List<?> getRecipesFor(RecipeType type) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        // var recipeManager = MinecraftServer.getServer().getRecipeManager();
        var minecraftServer = FallbackRecipeUtilImpl.GET_SERVER.invoke(null);
        var recipeManager = FallbackRecipeUtilImpl.GET_RECIPE_MANAGER.invoke(minecraftServer);
        // var type = BuiltInRegistries.RECIPE_TYPE.get(type.getId());
        var resourceLocation = FallbackRecipeUtilImpl.RESOURCE_LOCATION_CREATOR.invoke(null, type.getId(), ':');
        var registry = FallbackRecipeUtilImpl.RECIPE_TYPE_REGISTRY.get(null);
        var recipeType = FallbackRecipeUtilImpl.GET_RECIPE_TYPE_FROM_REGISTRY.invoke(registry, resourceLocation);
        // List<? extends RecipeHolder<?>> recipes = recipeManager.getAllRecipesFor(type)
        var recipes = FallbackRecipeUtilImpl.GET_ALL_RECIPES_FOR.invoke(recipeManager, recipeType);
        return (List<?>) recipes;
    }

    @Override
    public boolean hasNext() {
        return this.recipes.hasNext();
    }

    @Override
    public org.bukkit.inventory.Recipe next() {
        try {
            return (Recipe) FallbackRecipeUtilImpl.TO_BUKKIT_RECIPE.invoke(this.recipes.next());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove() {
        this.recipes.remove();
    }
}
