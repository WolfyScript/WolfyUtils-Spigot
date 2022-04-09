package com.wolfyscript.utilities.bukkit.chat;

import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.bukkit.adapters.PlayerImpl;
import me.wolfyscript.utilities.api.chat.ChatImplOld;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * This class implements the non-deprecated features of the Chat API and
 * combines the other deprecated methods by extending the {@link ChatImplOld}, which contains the deprecated implementations.
 */
public final class ChatImpl extends ChatImplOld {

    public ChatImpl(@NotNull WolfyUtils wolfyUtils) {
        super(wolfyUtils);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * *
     * New implementations using the Player adapter API. *
     * !Not checked! TODO: Check & Find better solutions *
     * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Override
    public void sendMessage(com.wolfyscript.utilities.common.adapters.Player player, Component component) {
        checkAndExc(player, player1 -> sendMessage(player, component));
    }

    @Override
    public void sendMessage(com.wolfyscript.utilities.common.adapters.Player player, boolean legacyColor, Component component) {
        checkAndExc(player, player1 -> sendMessage(player, legacyColor, component));
    }

    @Override
    public void sendMessages(com.wolfyscript.utilities.common.adapters.Player player, Component... components) {
        checkAndExc(player, player1 -> sendMessages(player, components));
    }

    @Override
    public void sendMessages(com.wolfyscript.utilities.common.adapters.Player player, boolean legacyColor, Component... components) {
        checkAndExc(player, player1 -> sendMessages(player, legacyColor, components));
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

}
