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

package me.wolfyscript.utilities.compatibility.plugins;

import me.wolfyscript.utilities.compatibility.PluginIntegration;
import me.wolfyscript.utilities.compatibility.plugins.itemsadder.CustomBlock;
import me.wolfyscript.utilities.compatibility.plugins.itemsadder.CustomStack;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface ItemsAdderIntegration extends PluginIntegration {

    String KEY = "ItemsAdder";

    @Deprecated
    @Nullable
    default CustomStack getByItemStack(ItemStack itemStack) {
        return getStackByItemStack(itemStack).orElse(null);
    }

    @Deprecated
    @Nullable
    default CustomStack getInstance(String namespacedID) {
        return getStackInstance(namespacedID).orElse(null);
    }

    Optional<CustomStack> getStackByItemStack(ItemStack itemStack);

    Optional<CustomStack> getStackInstance(String namespacedID);

    Optional<CustomBlock> getBlockByItemStack(ItemStack itemStack);

    Optional<CustomBlock> getBlockPlaced(Block block);

    Optional<CustomBlock> getBlockInstance(String namespacedID);
}
