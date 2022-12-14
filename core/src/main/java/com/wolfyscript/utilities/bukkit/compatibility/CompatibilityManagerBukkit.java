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

import com.wolfyscript.utilities.bukkit.WolfyUtilCore;
import java.util.HashMap;
import java.util.Map;

public final class CompatibilityManagerBukkit implements CompatibilityManager {


    private static final Map<String, Boolean> classes = new HashMap<>();
    private final WolfyUtilCore core;
    private final PluginsBukkit pluginsBukkit;
    private final boolean isPaper;

    public CompatibilityManagerBukkit(WolfyUtilCore core) {
        this.core = core;
        this.pluginsBukkit = new PluginsBukkit(core);
        this.isPaper = hasClass("com.destroystokyo.paper.utils.PaperPluginLogger");
    }

    public void init() {
        pluginsBukkit.init();
    }

    public Plugins getPlugins() {
        return pluginsBukkit;
    }

    @Override
    public boolean isPaper() {
        return isPaper;
    }

    /**
     * Check if the specific class exists.
     *
     * @param path The path to the class to check for.
     * @return If the class exists.
     */
    public static boolean hasClass(String path) {
        if (classes.containsKey(path)) {
            return classes.get(path);
        }
        try {
            Class.forName(path);
            classes.put(path, true);
            return true;
        } catch (Exception e) {
            classes.put(path, false);
            return false;
        }
    }
}
