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

import com.wolfyscript.utilities.bukkit.WolfyCoreImpl;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.gui.GuiHandler;
import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class InputCommand extends Command implements PluginIdentifiableCommand {

    private final WolfyCoreImpl core;

    public InputCommand(WolfyCoreImpl core) {
        super("wui");
        this.core = core;
        setUsage("/wui <input>");
        setDescription("Input for chat input actions");
    }

    @NotNull
    @Override
    public Plugin getPlugin() {
        return core;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        core.getAPIList().parallelStream()
                .filter(WolfyUtilsBukkit::hasInventoryAPI)
                .map(wolfyUtilities -> wolfyUtilities.getInventoryAPI().getGuiHandler(player))
                .filter(GuiHandler::isChatEventActive)
                .forEach(guiHandler -> Bukkit.getScheduler().runTask(WolfyCoreImpl.getInstance(), () -> {
                    //Handles ChatInput
                    if (!guiHandler.onChat(player, String.join(" ", args).trim(), args)) {
                        guiHandler.setChatInputAction(null);
                        guiHandler.openCluster();
                    }
                    if (guiHandler.isChatEventActive()) {
                        guiHandler.cancelChatInput();
                    }
                }));
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (!(sender instanceof Player player)) return List.of();
        return core.getAPIList().stream()
                .filter(WolfyUtilsBukkit::hasInventoryAPI)
                .map(wolfyUtilities -> wolfyUtilities.getInventoryAPI().getGuiHandler(player))
                .filter(guiHandler -> guiHandler.isChatEventActive() && guiHandler.hasChatTabComplete())
                .map(guiHandler -> guiHandler.getChatTabComplete().onTabComplete(guiHandler, player, args))
                .filter(Objects::nonNull).findFirst().orElseGet(() -> super.tabComplete(sender, alias, args));
    }
}
