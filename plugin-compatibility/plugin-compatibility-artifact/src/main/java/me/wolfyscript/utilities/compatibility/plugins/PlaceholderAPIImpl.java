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

package me.wolfyscript.utilities.compatibility.plugins;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.events.ExpansionsLoadedEvent;
import me.wolfyscript.utilities.annotations.WUPluginIntegration;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.compatibility.PluginIntegrationAbstract;
import me.wolfyscript.utilities.compatibility.plugins.placeholderapi.value_providers.ValueProviderFloatPAPI;
import me.wolfyscript.utilities.compatibility.plugins.placeholderapi.value_providers.ValueProviderIntegerPAPI;
import me.wolfyscript.utilities.compatibility.plugins.placeholderapi.value_providers.ValueProviderStringPAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@WUPluginIntegration(pluginName = PlaceholderAPIIntegration.KEY)
public class PlaceholderAPIImpl extends PluginIntegrationAbstract implements PlaceholderAPIIntegration, Listener {

    /**
     * The main constructor that is called whenever the integration is created.<br>
     *
     * @param core       The WolfyUtilCore.
     */
    protected PlaceholderAPIImpl(WolfyUtilCore core) {
        super(core, PlaceholderAPIIntegration.KEY);
    }

    @Override
    public void init(Plugin plugin) {
        getCore().getLogger().info("init PAPI event");
        Bukkit.getPluginManager().registerEvents(this, core);

        var valueProviderRegistry = core.getRegistries().getValueProviders();
        valueProviderRegistry.register(ValueProviderFloatPAPI.KEY, ValueProviderFloatPAPI.class);
        valueProviderRegistry.register(ValueProviderIntegerPAPI.KEY, ValueProviderIntegerPAPI.class);
        valueProviderRegistry.register(ValueProviderStringPAPI.KEY, ValueProviderStringPAPI.class);
    }

    @Override
    public boolean hasAsyncLoading() {
        return false;
    }

    @Override
    public @NotNull String setPlaceholders(OfflinePlayer player, @NotNull String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @Override
    public @NotNull List<String> setPlaceholders(OfflinePlayer player, @NotNull List<String> text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @Override
    public @NotNull String setPlaceholders(Player player, @NotNull String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @Override
    public @NotNull List<String> setPlaceholders(Player player, @NotNull List<String> text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @Override
    public @NotNull String setBracketPlaceholders(OfflinePlayer player, @NotNull String text) {
        return PlaceholderAPI.setBracketPlaceholders(player, text);
    }

    @Override
    public @NotNull List<String> setBracketPlaceholders(OfflinePlayer player, @NotNull List<String> text) {
        return PlaceholderAPI.setBracketPlaceholders(player, text);
    }

    @Override
    public String setBracketPlaceholders(Player player, @NotNull String text) {
        return PlaceholderAPI.setBracketPlaceholders(player, text);
    }

    @Override
    public List<String> setBracketPlaceholders(Player player, @NotNull List<String> text) {
        return PlaceholderAPI.setBracketPlaceholders(player, text);
    }

    @Override
    public String setRelationalPlaceholders(Player one, Player two, String text) {
        return PlaceholderAPI.setRelationalPlaceholders(one, two, text);
    }

    @Override
    public List<String> setRelationalPlaceholders(Player one, Player two, List<String> text) {
        return PlaceholderAPI.setRelationalPlaceholders(one, two, text);
    }

    @Override
    public boolean isRegistered(@NotNull String identifier) {
        return PlaceholderAPI.isRegistered(identifier);
    }

    @Override
    public Set<String> getRegisteredIdentifiers() {
        return PlaceholderAPI.getRegisteredIdentifiers();
    }

    @Override
    public Pattern getPlaceholderPattern() {
        return PlaceholderAPI.getPlaceholderPattern();
    }

    @Override
    public Pattern getBracketPlaceholderPattern() {
        return PlaceholderAPI.getBracketPlaceholderPattern();
    }

    @Override
    public Pattern getRelationalPlaceholderPattern() {
        return PlaceholderAPI.getRelationalPlaceholderPattern();
    }

    @Override
    public boolean containsPlaceholders(String text) {
        return PlaceholderAPI.containsPlaceholders(text);
    }

    @Override
    public boolean containsBracketPlaceholders(String text) {
        return PlaceholderAPI.containsBracketPlaceholders(text);
    }

    @EventHandler
    public void onEnabled(ExpansionsLoadedEvent event) {
        if (!this.isDoneLoading()) {
            this.markAsDoneLoading();
        }
    }

}
