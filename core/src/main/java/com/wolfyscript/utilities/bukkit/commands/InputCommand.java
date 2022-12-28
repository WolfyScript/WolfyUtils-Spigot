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
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.gui.GuiHandler;
import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InputCommand implements TabExecutor {

    private final WolfyCoreBukkit plugin;

    public InputCommand(WolfyCoreBukkit plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            plugin.getAPIList().parallelStream()
                    .filter(WolfyUtilsBukkit::hasInventoryAPI)
                    .map(wolfyUtilities -> wolfyUtilities.getInventoryAPI().getGuiHandler(player))
                    .filter(GuiHandler::isChatEventActive)
                    .forEach(guiHandler -> Bukkit.getScheduler().runTask(guiHandler.getWolfyUtils().getPlugin(), () -> {
                        //Handles ChatInput
                        if (!guiHandler.onChat(player, String.join(" ", args).trim(), args)) {
                            guiHandler.setChatInputAction(null);
                            guiHandler.openCluster();
                        }
                        if (guiHandler.isChatEventActive()) {
                            guiHandler.cancelChatInput();
                        }
                    }));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (sender instanceof Player player) {
            return plugin.getAPIList().stream()
                    .filter(WolfyUtilsBukkit::hasInventoryAPI)
                    .map(wolfyUtilities -> wolfyUtilities.getInventoryAPI().getGuiHandler(player))
                    .filter(guiHandler -> guiHandler.isChatEventActive() && guiHandler.hasChatTabComplete())
                    .map(guiHandler -> guiHandler.getChatTabComplete().onTabComplete(guiHandler, player, args)).filter(Objects::nonNull).findFirst().orElse(null);
        }
        return null;
    }
}
