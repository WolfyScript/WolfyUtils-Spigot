package com.wolfyscript.utilities.bukkit.chat;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.adapters.BukkitWrapper;
import com.wolfyscript.utilities.bukkit.adapters.PlayerImpl;
import com.wolfyscript.utilities.chat.Chat;
import com.wolfyscript.utilities.chat.ClickActionCallback;
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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This class implements the non-deprecated features of the Chat API and
 * combines the other deprecated methods by extending the , which contains the deprecated implementations.
 */
public class BukkitChat extends Chat {

    private static final Pattern ADVENTURE_PLACEHOLDER_PATTERN = Pattern.compile("([!?#]?)([a-z0-9_-]*)");
    private static final Pattern LEGACY_PLACEHOLDER_PATTERN = Pattern.compile("%([^%]+)%");
    private static final Map<UUID, PlayerAction> CLICK_DATA_MAP = new HashMap<>();

    private final Map<String, String> convertedLegacyPlaceholders = new HashMap<>();

    public BukkitChat(@NotNull WolfyUtilsBukkit wolfyUtils) {
        super(wolfyUtils);
    }

    @Override
    public void sendMessage(com.wolfyscript.utilities.platform.adapters.Player player, Component component) {
        wolfyUtils.getCore().getPlatform().getAudiences().player(player.uuid()).sendMessage(component);
    }

    @Override
    public void sendMessage(com.wolfyscript.utilities.platform.adapters.Player player, boolean prefix, Component component) {
        if (prefix) {
            component = getChatPrefix().append(component);
        }
        sendMessage(player, component);
    }

    @Override
    public void sendMessages(com.wolfyscript.utilities.platform.adapters.Player player, Component... components) {
        for (Component component : components) {
            sendMessage(player, component);
        }
    }

    @Override
    public void sendMessages(com.wolfyscript.utilities.platform.adapters.Player player, boolean prefix, Component... components) {
        for (Component component : components) {
            sendMessage(player, prefix, component);
        }
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

    private List<? extends TagResolver> getTemplates(Pair<String, String>[] replacements) {
        return Arrays.stream(replacements).map(pair -> Placeholder.parsed(convertOldPlaceholder(pair.getKey()), pair.getValue())).toList();
    }

    @Override
    public net.kyori.adventure.text.event.ClickEvent executable(com.wolfyscript.utilities.platform.adapters.Player player, boolean discard, ClickActionCallback actionCallback) {
        Preconditions.checkArgument(actionCallback != null, "The click action cannot be null!");
        UUID id;
        do {
            id = UUID.randomUUID();
        } while (CLICK_DATA_MAP.containsKey(id));
        CLICK_DATA_MAP.put(id, new PlayerAction((WolfyUtilsBukkit) wolfyUtils, player, actionCallback, discard));
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
                // remove invalid characters
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
