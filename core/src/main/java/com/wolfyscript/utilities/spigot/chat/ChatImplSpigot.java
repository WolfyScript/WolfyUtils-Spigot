package com.wolfyscript.utilities.spigot.chat;

import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.adapters.Player;
import com.wolfyscript.utilities.common.chat.Chat;
import me.wolfyscript.utilities.api.chat.ClickActionCallback;
import me.wolfyscript.utilities.util.NamespacedKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.List;

public class ChatImplSpigot extends Chat {

    public ChatImplSpigot(WolfyUtils wolfyUtils) {
        super(wolfyUtils);
    }

    @Override
    public void setChatPrefix(Component component) {

    }

    @Override
    public Component getChatPrefix() {
        return null;
    }

    @Override
    public MiniMessage getMiniMessage() {
        return null;
    }

    @Override
    public void sendMessage(Player player, Component component) {

    }

    @Override
    public void sendMessage(Player player, boolean b, Component component) {

    }

    @Override
    public void sendMessages(Player player, Component... components) {

    }

    @Override
    public void sendMessages(Player player, boolean b, Component... components) {

    }

    /**
     * @param player
     * @param namespacedKey
     * @param s
     * @deprecated
     */
    public void sendKey(Player player, NamespacedKey namespacedKey, String s) {

    }

    @Override
    public Component translated(String s) {
        return null;
    }

    @Override
    public Component translated(String s, boolean b) {
        return null;
    }

    @Override
    public Component translated(String s, List<? extends TagResolver> list) {
        return null;
    }

    @Override
    public Component translated(String s, boolean b, List<? extends TagResolver> list) {
        return null;
    }

    @Override
    public ClickEvent executable(Player player, boolean b, ClickActionCallback clickActionCallback) {
        return null;
    }

    @Override
    public String convertOldPlaceholder(String s) {
        return null;
    }
}
