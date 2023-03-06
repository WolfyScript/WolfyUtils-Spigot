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

package com.wolfyscript.utilities.bukkit.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;

/**
 * This adds the old backwards compatible methods that use the Bukkit {@link Player} instead of the
 * new adapter.
 * (Yes this could be an interface, but for backwards compatibility it must be a class!)
 * @deprecated Only contains deprecated methods! Use {@link com.wolfyscript.utilities.common.chat.Chat} instead!
 */
@Deprecated
public interface IBukkitChat {

    /**
     * Sends a chat component message, with the previously set prefix, to the player.
     *
     * @param player The player to send the message to.
     * @param component The component to send.
     */
    void sendMessage(Player player, Component component);

    /**
     * Sends a chat component message to the player.<br>
     * The prefix can be disabled, which just sends the component as is.
     *
     * @param player The player to send the message to.
     * @param prefix If the message should have the prefix.
     * @param component The component to send.
     */
    void sendMessage(Player player, boolean prefix, Component component);

    /**
     * Sends chat component messages to the player.<br>
     * Each message will be composed of the prefix and component.
     *
     * @param player The player to send the messages to.
     * @param components The components to send.
     */
    void sendMessages(Player player, Component... components);

    /**
     * Sends chat component messages to the player.<br>
     * If `prefix` is set to false, then the messages are just composed of the component.<br>
     * Otherwise, it does the same as {@link com.wolfyscript.utilities.common.chat.Chat#sendMessages(com.wolfyscript.utilities.common.adapters.Player, Component...)}
     *
     * @param player The player to send the messages to.
     * @param components The components to send.
     */
    void sendMessages(Player player, boolean prefix, Component... components);

    ClickEvent executable(Player player, boolean discard, ClickAction action);

}
