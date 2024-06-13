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

package me.wolfyscript.utilities.api.nms.v1_20_R4.inventory;

import java.util.Iterator;
import java.util.List;
import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeIterator implements Iterator<org.bukkit.inventory.Recipe> {

    private final Iterator<? extends RecipeHolder<?>> recipes;

    public RecipeIterator(RecipeType recipeType) {
        this.recipes = getRecipesFor(recipeType).iterator();
    }

    private List<? extends RecipeHolder<?>> getRecipesFor(RecipeType type) {
        var recipeManager = MinecraftServer.getServer().getRecipeManager();
        return switch (type) {
            case CRAFTING -> recipeManager.getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING);
            case SMELTING -> recipeManager.getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.SMELTING);
            case BLASTING -> recipeManager.getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.BLASTING);
            case SMOKING -> recipeManager.getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.SMOKING);
            case CAMPFIRE_COOKING -> recipeManager.getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CAMPFIRE_COOKING);
            case STONECUTTING -> recipeManager.getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.STONECUTTING);
            case SMITHING -> recipeManager.getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.SMITHING);
        };
    }

    @Override
    public boolean hasNext() {
        return this.recipes.hasNext();
    }

    @Override
    public org.bukkit.inventory.Recipe next() {
        return this.recipes.next().toBukkitRecipe();
    }

    @Override
    public void remove() {
        this.recipes.remove();
    }
}
