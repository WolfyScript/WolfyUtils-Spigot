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

package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonAction;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonRender;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackChatInput;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackChatTabComplete;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

/**
 * This object is used to store all relevant data for the Player using the GUI.<br>
 * The object persists across player disconnecting/rejoining, so that the state of the GUIs and cache are persistent as long as the server is running.
 * <br>
 * <br>
 * <strong>Data it stores per player:</strong>
 * <br>
 *
 */
@Deprecated(forRemoval = true)
public class GuiHandler<C extends CustomCache> implements Listener {

    private final WolfyUtilsBukkit api;
    private final InventoryAPI invAPI;
    private final UUID uuid;
    private CallbackChatInput chatInputAction = null;
    private CallbackChatTabComplete chatTabComplete = null;
    private GuiCluster cluster = null;
    private BukkitTask windowUpdateTask = null;

    public GuiHandler(Player player, WolfyUtilsBukkit api, InventoryAPI invAPI, C customCache) {
        this.api = api;
        this.invAPI = invAPI;
        this.uuid = player.getUniqueId();
        Bukkit.getPluginManager().registerEvents(this, api.getPlugin());
    }

    /**
     * Gets the WolfyUtils instance that this GuiHandler belongs to.
     *
     * @return The WolfyUtilities instance.
     */
    public WolfyUtilsBukkit getWolfyUtils() {
        return api;
    }

    /**
     * This method only returns null if the player is offline or not found!<br>
     * If called directly in {@link GuiWindow#onUpdateSync(GuiUpdate)}, {@link GuiWindow#onUpdateAsync(GuiUpdate)}, {@link CallbackButtonAction}, {@link CallbackButtonRender}, etc. the player should always be available.<br>
     * <strong>However, if called a few ticks later or in a scheduler, the returned value might be null, as the player might have disconnected.</strong>
     *
     * @return The active player of this handler, or null if the players is not found/offline.
     */
    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * @return If there is currently an active {@link CallbackChatInput}, that will be called on chat input.
     */
    public boolean isChatEventActive() {
        return getChatInputAction() != null;
    }

    /**
     * @return The active {@link CallbackChatInput} or null if not active.
     */
    @Nullable
    public CallbackChatInput getChatInputAction() {
        return chatInputAction;
    }

    /**
     * Set the {@link CallbackChatInput} to be called on next chat input.
     *
     * @param chatInputAction The new {@link CallbackChatInput}
     */
    public void setChatInputAction(CallbackChatInput chatInputAction) {
        this.chatInputAction = chatInputAction;
    }

    /**
     * Closes the current open window.
     */
    public void close() {
        getWindowUpdateTask().ifPresent(BukkitTask::cancel);
        var player = getPlayer();
        if (player != null) player.closeInventory();
    }

    public Optional<BukkitTask> getWindowUpdateTask() {
        return Optional.ofNullable(windowUpdateTask);
    }

}
