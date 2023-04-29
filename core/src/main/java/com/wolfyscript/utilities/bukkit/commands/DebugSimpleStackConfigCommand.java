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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wolfyscript.utilities.bukkit.WolfyCoreImpl;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemReference;
import com.wolfyscript.utilities.bukkit.world.items.reference.SimpleBukkitItemReference;
import java.io.File;
import java.io.IOException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class DebugSimpleStackConfigCommand extends Command implements PluginIdentifiableCommand {

    private final WolfyCoreImpl plugin;

    public DebugSimpleStackConfigCommand(WolfyCoreImpl plugin) {
        super("simple_bukkit_stack");
        this.plugin = plugin;
        setUsage("/simple_bukkit_stack");
        setDescription("DEBUG!");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (!player.hasPermission("wolfyutilities.command.simple_bukkit_stack_debug")) return true;
        if (!ItemUtils.isAirOrNull(player.getEquipment().getItemInMainHand())) {
            ItemReference reference = plugin.getRegistries().getItemReferences().parse(player.getEquipment().getItemInMainHand());
            System.out.println("Found Reference: " + reference);
            System.out.println(reference.getItem());
            if (reference instanceof SimpleBukkitItemReference itemReference) {
                System.out.println(itemReference.getConfig());
                try {
                    System.out.println(plugin.getWolfyUtils().getJacksonMapperUtil().getGlobalMapper().writeValueAsString(itemReference));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        }
        File file = new File(plugin.getWolfyUtils().getDataFolder(), "simple_bukkit_stack_debug.conf");
        if (file.exists()) {
            try {
                ItemReference reference = plugin.getWolfyUtils().getJacksonMapperUtil().getGlobalMapper().readValue(file, ItemReference.class);
                System.out.println("Reference: " + reference.toString());
                System.out.println(reference.getItem());
                ItemStack stack = reference.getItem();
                if (stack != null) {
                    player.getInventory().addItem(stack);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
