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

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiWindow;
import java.util.List;
import me.wolfyscript.utilities.api.chat.ClickAction;
import me.wolfyscript.utilities.api.chat.ClickData;
import me.wolfyscript.utilities.util.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This adds the old backwards compatible methods that use the Bukkit {@link Player} instead of the
 * new adapter.
 * (Yes this could be an interface, but for backwards compatibility it must be a class!)
 * @deprecated Only contains deprecated methods! Use {@link com.wolfyscript.utilities.common.chat.Chat} instead!
 */
@Deprecated
public interface IBukkitChat {

    /**
     * Sends a message to the player with legacy chat format.
     *
     * @deprecated Legacy chat format. This will convert the message multiple times (Not efficient!) {@link com.wolfyscript.utilities.common.chat.Chat#sendMessage(com.wolfyscript.utilities.common.adapters.Player, Component)} should be used instead!
     *             Use {@link com.wolfyscript.utilities.common.chat.Chat#translated(String)} or {@link com.wolfyscript.utilities.common.chat.Chat#translated(String, boolean)} to translate language keys!
     * @param player The player to send the message to.
     * @param message The message to send.
     */
    @Deprecated
    void sendMessage(Player player, String message);

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
     * Sends a message to the player with legacy chat format.
     *
     * @deprecated Legacy chat format. This will convert the message multiple times (Not efficient!) {@link com.wolfyscript.utilities.common.chat.Chat#sendMessage(com.wolfyscript.utilities.common.adapters.Player, Component)} should be used instead!
     *             Use {@link com.wolfyscript.utilities.common.chat.Chat#translated(String, List)} or {@link com.wolfyscript.utilities.common.chat.Chat#translated(String, boolean, List)} to translate language keys!
     * @param player The player to send the message to.
     * @param message The message to send.
     * @param replacements The placeholder values to replace in the message.
     */
    @Deprecated
    void sendMessage(Player player, String message, Pair<String, String>... replacements);

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
     * Otherwise, it does the same as {@link #sendMessages(com.wolfyscript.utilities.common.adapters.Player, Component...)}
     *
     * @param player The player to send the messages to.
     * @param components The components to send.
     */
    void sendMessages(Player player, boolean prefix, Component... components);

    /**
     * Sends messages to the player with legacy chat format.
     *
     * @deprecated Legacy chat format. This will convert the message multiple times (Not efficient!) {@link #sendMessage(com.wolfyscript.utilities.common.adapters.Player, Component)} should be used instead!
     *             Use {@link #translated(String)} or {@link #translated(String, boolean)} to translate language keys!
     *             Consider using the {@link GuiCluster#translatedMsgKey(String)} to get the translated global message from the cluster.
     * @param player The player to send the message to.
     * @param messages The messages to send.
     */
    @Deprecated
    void sendMessages(Player player, String... messages);

    /**
     * Sends a global message of the Cluster to the player.
     *
     * @deprecated Legacy chat format. This will convert the message multiple times (Not efficient!) {@link #sendMessage(com.wolfyscript.utilities.common.adapters.Player, Component)} should be used instead!
     *             Use {@link #translated(String)} or {@link #translated(String, boolean)} to translate language keys!
     *             Consider using the {@link GuiCluster#translatedMsgKey(String)} to get the translated global message from the cluster.
     * @param player The player to send the message to.
     * @param msgKey The key of the messages to send.
     */
    @Deprecated
    void sendKey(Player player, String clusterID, String msgKey);

    @Deprecated
    void sendKey(Player player, GuiCluster<?> guiCluster, String msgKey);

    @Deprecated
    void sendKey(Player player, @NotNull BukkitNamespacedKey windowKey, String msgKey);

    /**
     * Sends a global message of the Cluster to the player.
     *
     * @deprecated Legacy chat format. This will convert the message multiple times (Not efficient!) {@link #sendMessage(com.wolfyscript.utilities.common.adapters.Player, Component)} should be used instead!
     *             Consider using the {@link GuiCluster#translatedMsgKey(String)} to get the translated global message from the cluster.
     * @param player The player to send the message to.
     * @param replacements The placeholder values to replace in the message.
     */
    @Deprecated
    void sendKey(Player player, GuiCluster<?> guiCluster, String msgKey, Pair<String, String>... replacements);

    /**
     * Sends a message of the {@link GuiWindow} to the player.
     *
     * @deprecated Legacy chat format. This will convert the message multiple times (Not efficient!) {@link #sendMessage(com.wolfyscript.utilities.common.adapters.Player, Component)} should be used instead!
     *             Consider using the {@link GuiWindow#translatedMsgKey(String)} to get the translated message from the window.
     * @param player The player to send the message to.
     * @param msgKey The key of the messages to send.
     */
    @Deprecated
    void sendKey(Player player, BukkitNamespacedKey namespacedKey, String msgKey, Pair<String, String>... replacements);

    ClickEvent executable(Player player, boolean discard, ClickAction action);

    /**
     * Sends the clickable chat messages to the player.<br>
     * It allows you to also include ClickData with executable code.
     *
     * @deprecated This was mostly used to run code when a player clicks on a text in chat. That is now replaced by {@link #executable(Player, boolean, ClickAction)}, which can be used in combination of any {@link Component} and is way more flexible!
     *
     * @param player The player to send the message to.
     * @param clickData The click data of the message.
     */
    @Deprecated
    void sendActionMessage(Player player, ClickData... clickData);

    /**
     * Sends the clickable chat messages to the player.<br>
     * It allows you to also include ClickData with executable code.
     *
     * @deprecated This was mostly used to run code when a player clicks on a text in chat. That is now replaced by {@link #executable(Player, boolean, ClickAction)}, which can be used in combination of any {@link Component} and is way more flexible!
     *
     * @param player The player to send the message to.
     * @param clickData The click data of the message.
     */
    @Deprecated
    TextComponent[] getActionMessage(String prefix, Player player, ClickData... clickData);

    /**
     * @deprecated Replaced by {@link #getChatPrefix()}
     * @return The chat prefix as a String.
     */
    @Deprecated
    String getInGamePrefix();

    /**
     * @deprecated Replaced by {@link #setChatPrefix(Component)}
     * @param inGamePrefix The chat prefix.
     */
    @Deprecated
    void setInGamePrefix(String inGamePrefix);

}
