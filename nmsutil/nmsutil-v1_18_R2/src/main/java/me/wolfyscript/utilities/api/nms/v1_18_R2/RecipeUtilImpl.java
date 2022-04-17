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

package me.wolfyscript.utilities.api.nms.v1_18_R2;

import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.inventory.FunctionalBlastingRecipe;
import me.wolfyscript.utilities.api.nms.inventory.FunctionalCampfireRecipe;
import me.wolfyscript.utilities.api.nms.inventory.FunctionalCookingRecipe;
import me.wolfyscript.utilities.api.nms.inventory.FunctionalFurnaceRecipe;
import me.wolfyscript.utilities.api.nms.inventory.ExtendedRecipeChoice;
import me.wolfyscript.utilities.api.nms.inventory.RecipeType;
import me.wolfyscript.utilities.api.nms.v1_18_R2.item.crafting.FunctionalCampfireRecipeImpl;
import me.wolfyscript.utilities.api.nms.v1_18_R2.item.crafting.FunctionalFurnaceRecipeImpl;
import me.wolfyscript.utilities.api.nms.v1_18_R2.item.crafting.ExtendedRecipeChoiceImpl;
import me.wolfyscript.utilities.api.nms.v1_18_R2.inventory.RecipeIterator;
import me.wolfyscript.utilities.util.NamespacedKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
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
        return new ExtendedRecipeChoiceImpl(sourceCheck, choices);
    }

    @Override
    public ExtendedRecipeChoice recipeChoice(Function<ItemStack, Boolean> sourceCheck, @NotNull ItemStack choice) {
        return new ExtendedRecipeChoiceImpl(sourceCheck, choice);
    }

    @Override
    public ExtendedRecipeChoice recipeChoice(Function<ItemStack, Boolean> sourceCheck, @NotNull ItemStack... choices) {
        return new ExtendedRecipeChoiceImpl(sourceCheck, choices);
    }

    @Override
    public FunctionalFurnaceRecipe furnaceRecipe(NamespacedKey key, ItemStack result, ItemStack source, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> matchRecipe) {
        return new FunctionalFurnaceRecipeImpl(toMC(key), "", new ExtendedRecipeChoiceImpl(itemStack -> false, source).toNMS(), CraftItemStack.asNMSCopy(result), experience, cookingTime, matchRecipe);
    }

    @Override
    public FunctionalCampfireRecipe campfireRecipe(NamespacedKey key, ItemStack result, ItemStack source, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> matchRecipe) {
        return new FunctionalCampfireRecipeImpl(toMC(key), "", new ExtendedRecipeChoiceImpl(itemStack -> false, source).toNMS(), CraftItemStack.asNMSCopy(result), experience, cookingTime, matchRecipe);
    }

    @Override
    public FunctionalBlastingRecipe blastingRecipe(NamespacedKey key, ItemStack result, ItemStack source, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> recipeMatch) {
        return null;
    }

    @Override
    public void registerCookingRecipe(FunctionalCookingRecipe recipe) {
        if (recipe instanceof AbstractCookingRecipe mcRecipe) {
            MinecraftServer.getServer().getRecipeManager().addRecipe(mcRecipe);
        }
    }

    private ResourceLocation toMC(NamespacedKey key) {
        return new ResourceLocation(key.getNamespace(), key.getKey());
    }


}
