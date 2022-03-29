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

package me.wolfyscript.utilities.api;

import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.YamlConfiguration;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class WolfyUtilities extends WolfyUtilsBukkit {

    private static final Map<String, Boolean> classes = new HashMap<>();

    public static boolean hasJavaXScripting() {
        return hasClass("javax.script.ScriptEngine");
    }

    public static boolean hasSpigot() {
        return hasClass("org.spigotmc.Metrics");
    }

    public static boolean hasWorldGuard() {
        return hasPlugin("WorldGuard");
    }

    public static boolean hasPlotSquared() {
        return hasPlugin("PlotSquared");
    }

    public static boolean hasLWC() {
        return hasPlugin("LWC");
    }

    public static boolean hasMythicMobs() {
        return hasPlugin("MythicMobs");
    }

    public static boolean hasPlaceHolderAPI() {
        return hasPlugin("PlaceholderAPI");
    }

    public static boolean hasMcMMO() {
        return hasPlugin("mcMMO");
    }

    WolfyUtilities(WolfyCoreBukkit core, Plugin plugin, Class<? extends CustomCache> cacheType, boolean initialize) {
        super(core, plugin, cacheType, initialize);
    }

    WolfyUtilities(WolfyCoreBukkit core, Plugin plugin, Class<? extends CustomCache> customCacheClass) {
        this(core, plugin, customCacheClass, false);
    }

    WolfyUtilities(WolfyCoreBukkit core, Plugin plugin, boolean init) {
        this(core, plugin, CustomCache.class, init);
    }

    /**
     * Gets or create the {@link WolfyUtilities} instance for the specified plugin.
     *
     * @param plugin The plugin to get the instance from.
     * @return The WolfyUtilities instance for the plugin.
     */
    @Deprecated
    public static WolfyUtilities get(Plugin plugin) {
        return WolfyUtilCore.getInstance().getAPI(plugin);
    }

    @Deprecated
    public static WolfyUtilities get(Plugin plugin, boolean init) {
        return WolfyUtilCore.getInstance().getAPI(plugin, init);
    }

    /**
     * Gets or create the {@link WolfyUtilities} instance for the specified plugin.
     * This method also creates the InventoryAPI with the specified custom class of the {@link CustomCache}.
     *
     * @param plugin           The plugin to get the instance from.
     * @param customCacheClass The class of the custom cache you created. Must extend {@link CustomCache}
     * @return The WolfyUtilities instance for the plugin.
     */
    @Deprecated
    public static WolfyUtilities get(Plugin plugin, Class<? extends CustomCache> customCacheClass) {
        return WolfyUtilCore.getInstance().getAPI(plugin, customCacheClass);
    }

    /**
     * @param pluginName The name of the plugin to check for
     * @return If the plugin is loaded
     */
    public static boolean hasPlugin(String pluginName) {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
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

    /**
     * @return The internal WolfyUtilities plugin.
     */
    public static Plugin getWUPlugin() {
        return WolfyUtilCore.getInstance();
    }

    /**
     * @return The {@link WolfyUtilities} instance of WolfyUtilities internal plugin.
     */
    public static WolfyUtilities getWUCore() {
        return WolfyUtilities.get(getWUPlugin());
    }

    /**
     * @return The version of WolfyUtilities internal plugin and therefore also this API version.
     */
    public static String getVersion() {
        return getWUPlugin().getDescription().getVersion();
    }

    public static int getVersionNumber() {
        return Integer.parseInt(getVersion().replaceAll("[^0-9]", ""));
    }

    /**
     * Gets the {@link WolfyUtilCore}. This should be used instead of {@link WolfyUtilCore#getInstance()}} whenever possible!
     *
     * @return The core of the plugin.
     */
    @Override
    public WolfyUtilCore getCore() {
        return (WolfyUtilCore) super.getCore();
    }

    /**
     * @return The {@link me.wolfyscript.utilities.api.chat.Chat} instance.
     * @see me.wolfyscript.utilities.api.chat.Chat More information about Chat.
     */
    @Deprecated
    public me.wolfyscript.utilities.api.chat.Chat getChat() {
        return (Chat) super.getChat();
    }

    /**
     * This method uses the main {@link YamlConfiguration} with the key "config" to check if debug is enabled.
     *
     * @return True if the debug mode is enabled.
     * @see ConfigAPI#registerConfig(YamlConfiguration) More information about registration of configs.
     */
    public boolean hasDebuggingMode() {
        return getConfigAPI().getConfig("config") != null && getConfigAPI().getConfig("config").getBoolean("debug");
    }
}