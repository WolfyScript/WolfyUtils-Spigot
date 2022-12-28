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

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.gui.button.Button;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonItemInput;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonType;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import com.wolfyscript.utilities.bukkit.gui.callback.CallbackButtonAction;
import org.bukkit.inventory.Inventory;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryAPI<C extends CustomCache> implements Listener {

    private final Plugin plugin;
    private final WolfyUtilsBukkit wolfyUtilities;
    private final Map<UUID, GuiHandler<C>> guiHandlers = new HashMap<>();
    private final Map<String, GuiCluster<C>> guiClusters = new HashMap<>();
    final Class<C> customCacheClass;

    public InventoryAPI(Plugin plugin, WolfyUtilsBukkit wolfyUtilities, Class<C> customCacheClass) {
        this.wolfyUtilities = wolfyUtilities;
        this.plugin = plugin;
        this.customCacheClass = customCacheClass;
        getCacheInstance();
    }

    /**
     * Register a {@link GuiCluster}
     * If there is already a GuiCluster with the same key, then it will be replaced with the new value.
     *
     * @param guiCluster The {@link GuiCluster} to register.
     */
    public void registerCluster(GuiCluster<C> guiCluster) {
        guiClusters.putIfAbsent(guiCluster.getId(), guiCluster); //Make sure the cluster is registered before the init is called. Otherwise, buttons might fail to init!
        guiCluster.onInit();
    }

    /**
     * Get the {@link GuiCluster} by the key.
     *
     * @param clusterID The key of the {@link GuiCluster}.
     * @return The {@link GuiCluster} associated with the key. Null if none is found.
     */
    @Nullable
    public GuiCluster<C> getGuiCluster(String clusterID) {
        return guiClusters.get(clusterID);
    }

    public boolean hasGuiCluster(String clusterID) {
        return getGuiCluster(clusterID) != null;
    }

    public GuiWindow<C> getGuiWindow(NamespacedKey namespacedKey) {
        GuiCluster<C> cluster = getGuiCluster(namespacedKey.getNamespace());
        return cluster != null ? cluster.getGuiWindow(namespacedKey.getKey()) : null;
    }

    public WolfyUtilsBukkit getWolfyUtils() {
        return wolfyUtilities;
    }

    public void openCluster(Player player, String clusterID) {
        getGuiHandler(player).openCluster(clusterID);
    }

    public void openGui(Player player, NamespacedKey namespacedKey) {
        getGuiHandler(player).openWindow(namespacedKey);
    }

    public void removeGui(Player player) {
        if (hasGuiHandler(player)) {
            removePlayerGuiHandler(player);
        }
    }

    /**
     * Get or create the {@link GuiHandler} for this player.
     *
     * @param player The player for the GuiHandler
     * @return The GuiHandler for this player.
     */
    @NotNull
    public GuiHandler<C> getGuiHandler(Player player) {
        if (!hasGuiHandler(player)) {
            createGuiHandler(player);
        }
        return guiHandlers.get(player.getUniqueId());
    }

    private void createGuiHandler(Player player) {
        GuiHandler<C> guiHandler = new GuiHandler<>(player, wolfyUtilities, this, getCacheInstance());
        setPlayerGuiHandler(player, guiHandler);
    }

    private void setPlayerGuiHandler(Player player, GuiHandler<C> guiStudio) {
        guiHandlers.put(player.getUniqueId(), guiStudio);
    }

    private void removePlayerGuiHandler(Player player, GuiHandler<?> guiStudio) {
        guiHandlers.remove(player.getUniqueId(), guiStudio);
    }

    private void removePlayerGuiHandler(Player player) {
        guiHandlers.remove(player.getUniqueId());
    }

    public boolean hasGuiHandler(Player player) {
        return guiHandlers.containsKey(player.getUniqueId()) && guiHandlers.get(player.getUniqueId()) != null;
    }

    public boolean hasGuiHandlerAndWindow(Player player) {
        return guiHandlers.containsKey(player.getUniqueId()) && guiHandlers.get(player.getUniqueId()) != null && guiHandlers.get(player.getUniqueId()).getWindow() != null;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * This method will reset the entire GUI Buttons and re-initiates the GUIClusters afterwards.
     * Be careful when calling this method.
     * It's main purpose is to reload the GUI after the language was changed or other data changed that requires the buttons to re-initiate.
     */
    public void reset() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.closeInventory();
            removeGui(player);
        }
        guiHandlers.clear();
        guiClusters.forEach((s, guiCluster) -> {
            guiCluster.getButtons().clear();
            guiCluster.getGuiWindows().values().forEach(guiWindow -> guiWindow.buttons.clear());
        });
        guiClusters.forEach((s, cGuiCluster) -> cGuiCluster.onInit());
    }

    /**
     * Will create a new instance of the cache.
     * <br>
     * It's going to use the defined class from the constructor to create the cache.
     * <br>
     * <b>The cache requires a default constructor with no params!</b>, else if the constructor doesn't exist or other errors occur it will return null.
     *
     * @return A new instance of the cache, or null if there was an error (e.g. The cache class doesn't contain a default constructor).
     */
    public C getCacheInstance() {
        try {
            Constructor<C> constructor = this.customCacheClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets a globally registered Button.
     *
     * @param namespacedKey The namespaced key of the Button.
     * @return Button of the corresponding namespaced key
     */
    public Button<C> getButton(NamespacedKey namespacedKey) {
        if (namespacedKey == null) return null;
        GuiCluster<C> cluster = getGuiCluster(namespacedKey.getNamespace());
        return cluster != null ? cluster.getButton(namespacedKey.getKey()) : null;
    }

}
