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

package com.wolfyscript.utilities.bukkit.network.messages;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.nms.api.network.MCByteBuf;
import io.netty.buffer.Unpooled;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Reads and Writes a single registered Packet.
 *
 * <strong>This API is not final and still WIP! It is very experimental and for testing! The API might change completely in the future, so you shouldn't use it!</strong>
 */
public class Message implements PluginMessageListener {

    private final NamespacedKey channelTag;
    private final WolfyUtilsBukkit wolfyUtils;
    private final MessageConsumer decoder;

    public Message(NamespacedKey key, WolfyUtilsBukkit wolfyUtils, @Nullable MessageConsumer decoder) {
        this.channelTag = key;
        this.wolfyUtils = wolfyUtils;
        this.decoder = decoder;
        Plugin plugin = wolfyUtils.getPlugin();
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channelTag.toString());
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, channelTag.toString(), this);
    }

    Optional<MessageConsumer> getDecoder() {
        return Optional.ofNullable(decoder);
    }

    /**
     * Decodes the incoming packet using the defined {@link MessageConsumer}.<br>
     * If no decoder is specified it will ignore the incoming data.
     *
     * @param in The {@link MCByteBuf} of the incoming the data.
     * @param player
     */
    void decode(MCByteBuf in, Player player) {
        getDecoder().ifPresent(d -> d.accept(player, wolfyUtils, in));
    }

    void send(Player player, MCByteBuf bufOut) {
        player.sendPluginMessage(wolfyUtils.getPlugin(), channelTag.toString(), bufOut.array());
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals(channelTag.toString())) {
            return;
        }
        decode(wolfyUtils.getNmsUtil().getNetworkUtil().buffer(Unpooled.wrappedBuffer(message)), player);
    }
}
