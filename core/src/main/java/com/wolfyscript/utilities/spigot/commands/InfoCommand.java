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

package com.wolfyscript.utilities.spigot.commands;

import com.wolfyscript.utilities.spigot.WolfyUtilsSpigot;
import me.wolfyscript.utilities.main.WUPlugin;
import me.wolfyscript.utilities.util.version.ServerVersion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InfoCommand implements TabExecutor {

    private final WolfyUtilsSpigot plugin;

    public InfoCommand(WolfyUtilsSpigot plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        plugin.getWolfyUtilities().getChat().sendMessages((Player) sender, true,
                Component.text("——————— ", NamedTextColor.GRAY).append(Component.text("WolfyUtilities", NamedTextColor.AQUA, TextDecoration.BOLD)).append(Component.text(" ———————")),
                Component.empty(),
                Component.text("Author: ", NamedTextColor.GRAY).append(Component.text(String.join(", ", plugin.getDescription().getAuthors()), null, TextDecoration.BOLD)),
                Component.empty(),
                Component.text("Version: ", NamedTextColor.GRAY).append(Component.text(ServerVersion.getWUVersion().getVersion(), null, TextDecoration.BOLD)),
                Component.text("———————————————————————", NamedTextColor.GRAY)
        );
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
