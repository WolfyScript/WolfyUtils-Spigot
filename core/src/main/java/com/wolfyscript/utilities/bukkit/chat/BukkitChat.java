package com.wolfyscript.utilities.bukkit.chat;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.adapters.PlayerImpl;
import com.wolfyscript.utilities.common.chat.Chat;
import com.wolfyscript.utilities.common.chat.ClickActionCallback;
import com.wolfyscript.utilities.tuple.Pair;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This class implements the non-deprecated features of the Chat API and
 * combines the other deprecated methods by extending the , which contains the deprecated implementations.
 */
public class BukkitChat extends Chat implements IBukkitChat {

    private static final Pattern ADVENTURE_PLACEHOLDER_PATTERN = Pattern.compile("([!?#]?)([a-z0-9_-]*)");
    private static final Pattern LEGACY_PLACEHOLDER_PATTERN = Pattern.compile("%([^%]+)%");
    private static final Map<UUID, PlayerAction> CLICK_DATA_MAP = new HashMap<>();

    private final Map<String, String> convertedLegacyPlaceholders = new HashMap<>();
    private final LegacyComponentSerializer LEGACY_SERIALIZER;
    private final BungeeComponentSerializer BUNGEE_SERIALIZER;

    public BukkitChat(@NotNull WolfyUtilsBukkit wolfyUtils) {
        super(wolfyUtils);
        this.LEGACY_SERIALIZER = BukkitComponentSerializer.legacy();
        this.BUNGEE_SERIALIZER = BungeeComponentSerializer.get();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * *
     * New implementations using the Player adapter API. *
     * !Not checked! TODO: Check & Find better solutions *
     * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void sendMessage(com.wolfyscript.utilities.common.adapters.Player player, Component component) {
        checkAndExc(player, player1 -> sendMessage(player1, component));
    }

    @Override
    public void sendMessage(com.wolfyscript.utilities.common.adapters.Player player, boolean legacyColor, Component component) {
        checkAndExc(player, player1 -> sendMessage(player1, legacyColor, component));
    }

    @Override
    public void sendMessages(com.wolfyscript.utilities.common.adapters.Player player, Component... components) {
        checkAndExc(player, player1 -> sendMessages(player1, components));
    }

    @Override
    public void sendMessages(com.wolfyscript.utilities.common.adapters.Player player, boolean legacyColor, Component... components) {
        checkAndExc(player, player1 -> sendMessages(player1, legacyColor, components));
    }

    @Override
    public Component translated(String key) {
        return languageAPI.getComponent(key);
    }

    @Override
    public Component translated(String key, TagResolver... tagResolvers) {
        return languageAPI.getComponent(key, tagResolvers);
    }

    @Override
    public Component translated(String key, TagResolver tagResolver) {
        return languageAPI.getComponent(key, tagResolver);
    }

    /**
     * Used to check if the provided adapter is actually the Bukkit implementation.
     * If it is the bukkit implementation, then it runs the provided consumer.
     *
     * @param playerAdapter The Player adapter to use.
     * @param consumer The task to run when the bukkit player is available.
     */
    private void checkAndExc(com.wolfyscript.utilities.common.adapters.Player playerAdapter, Consumer<Player> consumer) {
        if (playerAdapter instanceof PlayerImpl bukkitPlayer) {
            consumer.accept(bukkitPlayer.getBukkitRef());
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
        CLICK_DATA_MAP.put(id, new PlayerAction((WolfyUtilsBukkit) wolfyUtils, player, action, discard));
        return net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, "/wua " + id);
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
