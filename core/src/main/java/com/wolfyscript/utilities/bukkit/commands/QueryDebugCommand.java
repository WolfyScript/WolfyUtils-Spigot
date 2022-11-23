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

package com.wolfyscript.utilities.bukkit.commands;

import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.nbt.NBTQuery;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import java.io.File;
import java.util.List;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QueryDebugCommand implements TabExecutor {

    private final WolfyCoreBukkit plugin;

    public QueryDebugCommand(WolfyCoreBukkit plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (!player.hasPermission("wolfyutilities.command.query_debug")) return true;
        ItemStack stack = player.getEquipment().getItem(EquipmentSlot.HAND);
        if (!ItemUtils.isAirOrNull(stack)) {
            File file = new File(plugin.getDataFolder(), "query_debug.json");
            if (file.exists()) {
                NBTQuery.of(file).ifPresent(nbtQuery -> {
                    NBTItem nbtItem = new NBTItem(stack);
                    NBTCompound result = nbtQuery.run(nbtItem);

                    System.out.println(result.toString());
                    if (args.length > 0) {
                        if (args[0].equalsIgnoreCase("true")) {
                            ItemStack stackToMergeIn = player.getEquipment().getItem(EquipmentSlot.OFF_HAND);
                            NBTItem nbtItem1 = new NBTItem(stackToMergeIn);
                            nbtItem1.mergeCompound(result);
                            nbtItem1.applyNBT(stackToMergeIn);
                        }
                    }
                });
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
