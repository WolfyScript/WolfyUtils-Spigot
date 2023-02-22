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

package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@Deprecated(forRemoval = true)
public class InventoryAPI implements Listener {

    private final Plugin plugin;
    private final WolfyUtilsBukkit wolfyUtilities;
    final Class<?> customCacheClass;

    public InventoryAPI(Plugin plugin, WolfyUtilsBukkit wolfyUtilities, Class<?> customCacheClass) {
        this.wolfyUtilities = wolfyUtilities;
        this.plugin = plugin;
        this.customCacheClass = customCacheClass;
    }

    public WolfyUtilsBukkit getWolfyUtils() {
        return wolfyUtilities;
    }

    public Plugin getPlugin() {
        return plugin;
    }

}
