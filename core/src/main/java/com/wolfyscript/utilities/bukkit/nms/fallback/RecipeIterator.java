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
import me.wolfyscript.utilities.util.Reflection;
import org.bukkit.inventory.Recipe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static me.wolfyscript.utilities.util.Reflection.NMSMapping.of;

public class RecipeIterator implements Iterator<org.bukkit.inventory.Recipe> {

    static boolean failedToInitialize = false;

    static Class<?> MINECRAFT_SERVER_CLASS;
    static Class<?> RECIPE_HOLDER;
    static Class<?> RECIPE_MANAGER;
    static Class<?> RECIPE_TYPE;
    static Class<?> BUILT_IN_REGISTRIES;
    static Class<?> REGISTRY;
    static Class<?> RESOURCE_LOCATION;
    static Method GET_SERVER;
    static Method GET_RECIPE_MANAGER;
    static Method GET_ALL_RECIPES_FOR;
    static Method TO_BUKKIT_RECIPE;
    static Method GET_RECIPE_TYPE_FROM_REGISTRY;
    static Field RECIPE_TYPE_REGISTRY;
    static Method RESOURCE_LOCATION_CREATOR;

    static {
        try {
            MINECRAFT_SERVER_CLASS = Reflection.getNMSUnsafe("server.MinecraftServer");
            RECIPE_HOLDER = Reflection.getNMSUnsafe("world.item.crafting.RecipeHolder");
            RECIPE_MANAGER = Reflection.getNMSUnsafe("world.item.crafting", of("CraftingManager").mojang("RecipeManager"));
            RECIPE_TYPE = Reflection.getNMSUnsafe("world.item.crafting", of("Recipes").mojang("RecipeType"));
            BUILT_IN_REGISTRIES = Reflection.getNMSUnsafe("core.registries.BuiltInRegistries");
            REGISTRY = Reflection.getNMSUnsafe("core", of("IRegistry").mojang("Registry"));
            RESOURCE_LOCATION = Reflection.getNMSUnsafe("resources", of("MinecraftKey").mojang("ResourceLocation"));

            GET_SERVER = Reflection.getDeclaredMethodUnsafe(MINECRAFT_SERVER_CLASS, "getServer");
            GET_RECIPE_MANAGER = Reflection.getDeclaredMethodUnsafe(MINECRAFT_SERVER_CLASS, of("aJ").mojang("getRecipeManager").get());
            GET_ALL_RECIPES_FOR = Reflection.getDeclaredMethodUnsafe(RECIPE_MANAGER, of("a").mojang("getAllRecipesFor").get(), RECIPE_TYPE);
            TO_BUKKIT_RECIPE = Reflection.getDeclaredMethodUnsafe(RECIPE_HOLDER, "toBukkitRecipe");
            GET_RECIPE_TYPE_FROM_REGISTRY = Reflection.getDeclaredMethodUnsafe(REGISTRY, of("a").mojang("get").get(), RESOURCE_LOCATION);

            RECIPE_TYPE_REGISTRY = Reflection.getDeclaredFieldUnsafe(BUILT_IN_REGISTRIES, of("q").mojang("RECIPE_TYPE").get());

            RESOURCE_LOCATION_CREATOR = Reflection.getDeclaredMethodUnsafe(RESOURCE_LOCATION, of("a").mojang("of").get(), String.class, Character.TYPE);
        } catch (Exception e) {
            // Fallback to empty iterator when it fails to find methods using reflection
            failedToInitialize = true;
        }
    }

    private final Iterator<?> recipes; // List of RecipeHolder instances

    public RecipeIterator(RecipeType recipeType) {
        if (failedToInitialize) {
            this.recipes = Collections.emptyIterator();
            return;
        }
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
        var minecraftServer = GET_SERVER.invoke(null);
        var recipeManager = GET_RECIPE_MANAGER.invoke(minecraftServer);
        // var type = BuiltInRegistries.RECIPE_TYPE.get(type.getId());
        var resourceLocation = RESOURCE_LOCATION_CREATOR.invoke(null, type.getId(), ':');
        var registry = RECIPE_TYPE_REGISTRY.get(null);
        var recipeType = GET_RECIPE_TYPE_FROM_REGISTRY.invoke(registry, resourceLocation);
        // List<? extends RecipeHolder<?>> recipes = recipeManager.getAllRecipesFor(type)
        var recipes = GET_ALL_RECIPES_FOR.invoke(recipeManager, recipeType);
        return (List<?>) recipes;
    }

    @Override
    public boolean hasNext() {
        return this.recipes.hasNext();
    }

    @Override
    public org.bukkit.inventory.Recipe next() {
        if (failedToInitialize) {
            throw new NoSuchElementException();
        }
        try {
            return (Recipe) TO_BUKKIT_RECIPE.invoke(this.recipes.next());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove() {
        this.recipes.remove();
    }
}
