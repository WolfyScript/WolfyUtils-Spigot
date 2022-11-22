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

package me.wolfyscript.utilities.api.chat;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.common.WolfyUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import me.wolfyscript.utilities.util.Pair;
import me.wolfyscript.utilities.util.chat.ChatColor;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the old deprecated methods available in the deprecated {@link Chat} class.
 */
@Deprecated
public abstract class ChatImplOld extends me.wolfyscript.utilities.api.chat.Chat {

    private static final Pattern ADVENTURE_PLACEHOLDER_PATTERN = Pattern.compile("([!?#]?)([a-z0-9_-]*)");
    private static final Pattern LEGACY_PLACEHOLDER_PATTERN = Pattern.compile("%([^%]+)%");
    protected static final Map<UUID, PlayerAction> CLICK_DATA_MAP = new HashMap<>();

    private final Map<String, String> convertedLegacyPlaceholders = new HashMap<>();
    private final LegacyComponentSerializer LEGACY_SERIALIZER;
    private final BungeeComponentSerializer BUNGEE_SERIALIZER;

    protected ChatImplOld(@NotNull WolfyUtils wolfyUtils) {
        super(wolfyUtils);
        this.LEGACY_SERIALIZER = BukkitComponentSerializer.legacy();
        this.BUNGEE_SERIALIZER = BungeeComponentSerializer.get();
    }

    /**
     * Sends a message to the player with legacy chat format.
     *
     * @param player  The player to send the message to.
     * @param message The message to send.
     * @deprecated Legacy chat format. This will convert the message multiple times (Not efficient!) {@link #sendMessage(Player, Component)} should be used instead!
     */
    @Deprecated
    @Override
    public void sendMessage(Player player, String message) {
        if (player != null) {
            sendMessage(player, true, LEGACY_SERIALIZER.deserialize(ChatColor.convert(languageAPI.replaceKeys(message))));
        }
    }

    @Override
    public void sendMessage(Player player, Component component) {
        if (player != null) {
            sendMessage(player, true, component);
        }
    }

    @Override
    public void sendMessage(Player player, boolean prefix, Component component) {
        if (player != null) {
            if (prefix) {
                component = getChatPrefix().append(Component.text(" ")).append(component);
            }
            player.spigot().sendMessage(BUNGEE_SERIALIZER.serialize(component));
        }
    }

    @SafeVarargs
    @Deprecated
    @Override
    public final void sendMessage(Player player, String message, Pair<String, String>... replacements) {
        if (player == null) return;
        if (replacements != null) {
            message = getInGamePrefix() + languageAPI.replaceColoredKeys(message);
            for (Pair<String, String> pair : replacements) {
                message = message.replaceAll(pair.getKey(), pair.getValue());
            }
        }
        player.sendMessage(ChatColor.convert(message));
    }

    @Override
    public void sendMessages(Player player, Component... components) {
        if (player != null) {
            for (Component component : components) {
                player.spigot().sendMessage(BUNGEE_SERIALIZER.serialize(component));
            }
        }
    }

    @Override
    public void sendMessages(Player player, boolean prefix, Component... components) {
        for (Component component : components) {
            sendMessage(player, prefix, component);
        }
    }

    /**
     * Sends multiple messages to the player with legacy chat format.
     *
     * @param player   The player to send the message to.
     * @param messages The messages to send.
     * @deprecated Legacy chat format. This will convert the messages multiple times (Not efficient!) {@link #sendMessages(Player, Component...)} should be used instead!
     */
    @Deprecated
    @Override
    public void sendMessages(Player player, String... messages) {
        if (player != null) {
            for (String message : messages) {
                sendMessage(player, true, LEGACY_SERIALIZER.deserialize(ChatColor.convert(languageAPI.replaceKeys(message))));
            }
        }
    }

    /**
     * Sends a global message of the Cluster to the player.
     *
     * @param player
     * @param clusterID
     * @param msgKey
     */
    @Deprecated
    @Override
    public void sendKey(Player player, String clusterID, String msgKey) {
        sendMessage(player, translated("inventories." + clusterID + ".global_messages." + msgKey, true));
    }

    /**
     * Sends a global message of the Cluster to the player.
     *
     * @param player
     * @param guiCluster
     * @param msgKey
     */
    @Deprecated
    @Override
    public void sendKey(Player player, GuiCluster<?> guiCluster, String msgKey) {
        sendMessage(player, translated("inventories." + guiCluster.getId() + ".global_messages." + msgKey, true));
    }

    @Deprecated
    @Override
    public void sendKey(Player player, @NotNull BukkitNamespacedKey windowKey, String msgKey) {
        sendMessage(player, translated("inventories." + windowKey.getNamespace() + "." + windowKey.getKey() + ".messages." + msgKey, true));
    }

    @Deprecated
    @SafeVarargs
    @Override
    public final void sendKey(Player player, GuiCluster<?> guiCluster, String msgKey, Pair<String, String>... replacements) {
        sendMessage(player, translated("inventories." + guiCluster.getId() + ".global_messages." + msgKey, true, getTemplates(replacements)));
    }

    @Deprecated
    @SafeVarargs
    @Override
    public final void sendKey(Player player, BukkitNamespacedKey namespacedKey, String msgKey, Pair<String, String>... replacements) {
        sendMessage(player, translated("inventories." + namespacedKey.getNamespace() + "." + namespacedKey.getKey() + ".messages." + msgKey, true, getTemplates(replacements)));
    }

    private List<? extends TagResolver> getTemplates(Pair<String, String>[] replacements) {
        return Arrays.stream(replacements).map(pair -> Placeholder.parsed(convertOldPlaceholder(pair.getKey()), pair.getValue())).toList();
    }

    @Override
    public net.kyori.adventure.text.event.ClickEvent executable(com.wolfyscript.utilities.common.adapters.Player player, boolean b, ClickActionCallback clickActionCallback) {
        return null;
    }

    /**
     * Creates a ClickEvent, that executes code when clicked.<br>
     * It will internally link a command with an id to the code to execute.
     * That internal command can only be executed by the player, which the message was sent to.
     *
     * @param player  The player the event belongs to.
     * @param discard If it should be discarded after clicked. (Any action is removed, when the player disconnects!)
     * @param action  The action to execute on click.
     * @return The ClickEvent with the generated command.
     */
    @Override
    public net.kyori.adventure.text.event.ClickEvent executable(Player player, boolean discard, ClickAction action) {
        Preconditions.checkArgument(action != null, "The click action cannot be null!");
        UUID id;
        do {
            id = UUID.randomUUID();
        } while (CLICK_DATA_MAP.containsKey(id));
        CLICK_DATA_MAP.put(id, new PlayerAction(((WolfyUtilities) wolfyUtils), player, action, discard));
        return net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/wua " + id);
    }

    /**
     * Sends the clickable chat messages to the player.<br>
     * It allows you to also include ClickData with executable code.
     *
     * @param player    The player to send the message to.
     * @param clickData The click data of the message.
     * @deprecated This was mostly used to run code, when a player clicks on a text in chat. That is now replaced by {@link #executable(Player, boolean, ClickAction)}, which can be used in combination of any {@link Component} and is way more flexible!
     */
    @Deprecated
    @Override
    public void sendActionMessage(Player player, ClickData... clickData) {
        TextComponent[] textComponents = getActionMessage(getInGamePrefix(), player, clickData);
        player.spigot().sendMessage(textComponents);
    }

    @Deprecated
    @Override
    public TextComponent[] getActionMessage(String prefix, Player player, ClickData... clickData) {
        TextComponent[] textComponents = new TextComponent[clickData.length + 1];
        textComponents[0] = new TextComponent(prefix == null ? "" : prefix);
        for (int i = 1; i < textComponents.length; i++) {
            ClickData data = clickData[i - 1];
            TextComponent component = new TextComponent(languageAPI.replaceColoredKeys(data.getMessage()));
            if (data.getClickAction() != null) {
                UUID id;
                do {
                    id = UUID.randomUUID();
                } while(CLICK_DATA_MAP.containsKey(id));
                CLICK_DATA_MAP.put(id, new PlayerAction(((WolfyUtilities) wolfyUtils), player, data));
                component.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/wua " + id));
            }
            for (ChatEvent<?, ?> chatEvent : data.getChatEvents()) {
                if (chatEvent instanceof HoverEvent) {
                    component.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(((HoverEvent) chatEvent).getAction(), ((HoverEvent) chatEvent).getValue()));
                } else if (chatEvent instanceof ClickEvent) {
                    component.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(((ClickEvent) chatEvent).getAction(), ((ClickEvent) chatEvent).getValue()));
                }
            }
            textComponents[i] = component;
        }
        return textComponents;
    }

    /**
     * @return The chat prefix as a String.
     * @deprecated Replaced by {@link #getChatPrefix()}
     */
    @Override
    public String getInGamePrefix() {
        return LEGACY_SERIALIZER.serialize(getChatPrefix());
    }

    /**
     * @param inGamePrefix The chat prefix.
     * @deprecated Replaced by {@link #setChatPrefix(Component)}
     */
    @Override
    public void setInGamePrefix(String inGamePrefix) {
        this.setChatPrefix(BukkitComponentSerializer.legacy().deserialize(inGamePrefix.trim()));
    }

    public static void removeClickData(UUID uuid) {
        CLICK_DATA_MAP.remove(uuid);
    }

    public static PlayerAction getClickData(UUID uuid) {
        return CLICK_DATA_MAP.get(uuid);
    }

    /**
     * Converts the old placeholder to a new mini-message compatible tag.<br>
     * If the placeholder wasn't already converted, it converts it and returns it.<br>
     * Otherwise, it uses the cached value and returns it.
     *
     * @param oldPlaceholder The old placeholder, that might be incompatible with mini-message.
     * @return The converted mini-message compatible placeholder.
     */
    @Override
    public String convertOldPlaceholder(String oldPlaceholder) {
        return convertedLegacyPlaceholders.computeIfAbsent(oldPlaceholder, placeholder -> {
            //Placeholder wasn't converted yet, lets convert it and cache it.
            Matcher matcher = LEGACY_PLACEHOLDER_PATTERN.matcher(placeholder);
            if (matcher.matches()) {
                placeholder = matcher.group(1);
            }
            //Going to make it lowercase, as that is required anyway.
            placeholder = placeholder.toLowerCase(Locale.ROOT);
            //Make sure it matches the mini-message tag pattern.
            Matcher adventureMatcher = ADVENTURE_PLACEHOLDER_PATTERN.matcher(placeholder);
            if (!adventureMatcher.matches()) {
                //remove invalid characters
                var builder = new StringBuilder();
                char[] chars = placeholder.toCharArray();
                boolean passedFirstGroup = false;
                for (char currentChar : chars) {
                    Matcher charMatch = ADVENTURE_PLACEHOLDER_PATTERN.matcher(String.valueOf(currentChar));
                    if (!charMatch.matches()) continue;
                    if (!passedFirstGroup) {
                        passedFirstGroup = true;
                    }
                    if (!charMatch.group(1).isEmpty()) continue;
                    builder.append(currentChar);
                }
                return builder.toString();
            }
            return placeholder;
        });
    }

    @Override
    public String getConsolePrefix() {
        return "[" + wolfyUtils.getName() + "]";
    }

    @Override
    public void sendConsoleMessage(String message) {
        ((WolfyUtilities) wolfyUtils).getConsole().info(message);
    }

    @Override
    public void sendConsoleMessage(String message, String... replacements) {
        ((WolfyUtilities) wolfyUtils).getConsole().log(Level.INFO, message, replacements);
    }

    @Override
    public void sendConsoleMessage(String message, String[]... replacements) {
        ((WolfyUtilities) wolfyUtils).getConsole().log(Level.INFO, message, replacements);
    }

    @Override
    public void sendConsoleWarning(String message) {
        ((WolfyUtilities) wolfyUtils).getConsole().warn(message);
    }

    @Override
    public void sendDebugMessage(String message) {
        ((WolfyUtilities) wolfyUtils).getConsole().debug(message);
    }

    /**
     * Listener for chat specific features.
     */
    public static class ChatListener implements Listener {

        @EventHandler
        public void actionRemoval(PlayerQuitEvent event) {
            CLICK_DATA_MAP.keySet().removeIf(uuid -> CLICK_DATA_MAP.get(uuid).getUuid().equals(event.getPlayer().getUniqueId()));
        }
    }

}
