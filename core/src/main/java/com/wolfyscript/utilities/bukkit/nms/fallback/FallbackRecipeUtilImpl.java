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
import me.wolfyscript.utilities.util.Reflection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;

import static me.wolfyscript.utilities.util.Reflection.NMSMapping.of;

public class FallbackRecipeUtilImpl extends me.wolfyscript.utilities.api.nms.RecipeUtil {

    static Class<?> MINECRAFT_SERVER_CLASS = Reflection.getNMS("server.MinecraftServer");
    static Class<?> RECIPE_HOLDER = Reflection.getNMS("world.item.crafting.RecipeHolder");
    static Class<?> RECIPE_MANAGER = Reflection.getNMS("world.item.crafting", of("CraftingManager").mojang("RecipeManager"));
    static Class<?> RECIPE_TYPE = Reflection.getNMS("world.item.crafting", of("Recipes").mojang("RecipeType"));
    static Class<?> BUILT_IN_REGISTRIES = Reflection.getNMS("core.registries.BuiltInRegistries");
    static Class<?> REGISTRY = Reflection.getNMS("core", of("IRegistry").mojang("Registry"));
    static Class<?> RESOURCE_LOCATION = Reflection.getNMS("resources", of("MinecraftKey").mojang("ResourceLocation"));

    static Method GET_SERVER = Reflection.getDeclaredMethod(false, MINECRAFT_SERVER_CLASS, "getServer");
    static Method GET_RECIPE_MANAGER = Reflection.getDeclaredMethod(false, MINECRAFT_SERVER_CLASS, of("aJ").mojang("getRecipeManager").get());
    static Method GET_ALL_RECIPES_FOR = Reflection.getDeclaredMethod(false, RECIPE_MANAGER, of("a").mojang("getAllRecipesFor").get(), RECIPE_TYPE);
    static Method TO_BUKKIT_RECIPE = Reflection.getDeclaredMethod(false, RECIPE_HOLDER, "toBukkitRecipe");
    static Method GET_RECIPE_TYPE_FROM_REGISTRY = Reflection.getDeclaredMethod(false, REGISTRY, of("a").mojang("get").get(), RESOURCE_LOCATION);

    static Field RECIPE_TYPE_REGISTRY = Reflection.getDeclaredField(BUILT_IN_REGISTRIES, of("q").mojang("RECIPE_TYPE").get());

    static Method RESOURCE_LOCATION_CREATOR = Reflection.getDeclaredMethod(false, RESOURCE_LOCATION, of("a").mojang("of").get(), String.class, Character.TYPE);

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
