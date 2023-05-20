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

package me.wolfyscript.utilities.compatibility;

import com.wolfyscript.utilities.bukkit.nms.ServerProperties;
import java.util.Properties;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.util.version.MinecraftVersion;
import me.wolfyscript.utilities.util.version.ServerVersion;

public final class CompatibilityManagerBukkit implements CompatibilityManager {

    private boolean has1_20Features = false;
    private final WolfyUtilCore core;
    private final PluginsBukkit pluginsBukkit;

    public CompatibilityManagerBukkit(WolfyUtilCore core) {
        this.core = core;
        this.pluginsBukkit = new PluginsBukkit(core);
    }

    public void init() {
        pluginsBukkit.init();
        Properties properties = ServerProperties.get();
        has1_20Features = ServerVersion.getVersion().isAfterOrEq(MinecraftVersion.of(1, 20, 0));
        // If the version is already 1.20 or later, then it has 1.20 features!
        if (!has1_20Features && ServerVersion.getVersion().isAfterOrEq(MinecraftVersion.of(1, 19, 4))) {
            String initialEnabledDataPacks = properties.getProperty("initial-enabled-packs", "vanilla");
            for (String s : initialEnabledDataPacks.split(",")) {
                if (s.equalsIgnoreCase("update_1_20")) {
                    has1_20Features = true;
                    break;
                }
            }
        }
    }

    public boolean has1_20Features() {
        return has1_20Features;
    }

    public Plugins getPlugins() {
        return pluginsBukkit;
    }
}
