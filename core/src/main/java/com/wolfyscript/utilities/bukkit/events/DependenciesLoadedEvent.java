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

package com.wolfyscript.utilities.bukkit.events;

import com.wolfyscript.utilities.bukkit.WolfyUtilBootstrap;
import com.wolfyscript.utilities.bukkit.compatibility.PluginIntegration;
import com.wolfyscript.utilities.common.WolfyCore;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is called once the {@link PluginIntegration}s of all plugins that WolfyUtilities depends on are done.
 * That includes plugins that load data asynchronously.
 */
public class DependenciesLoadedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final WolfyCore core;

    public DependenciesLoadedEvent(WolfyCore core) {
        this.core = core;
    }

    /**
     * Gets the core {@link WolfyUtilBootstrap}
     * @return The core of the plugin.
     */
    public WolfyCore getCore() {
        return core;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
