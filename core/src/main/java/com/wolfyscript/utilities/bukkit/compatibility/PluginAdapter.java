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

package com.wolfyscript.utilities.bukkit.compatibility;

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;

/**
 * These adapters are used for easier management of plugin dependent classes.<br>
 * e.g. If you have a soft-depend and need to only register an object when it is available.
 *
 */
public abstract class PluginAdapter {

    private final BukkitNamespacedKey key;

    protected PluginAdapter(BukkitNamespacedKey namespacedKey) {
        this.key = namespacedKey;
    }

    public final BukkitNamespacedKey getNamespacedKey() {
        return key;
    }

}
