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
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.gui.button.Button;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Contains all the data that is used in {@link GuiWindow} updates like {@link GuiWindow#onUpdateAsync(GuiUpdate)} or {@link GuiWindow#onUpdateSync(GuiUpdate)}.
 * <br>
 * It is used to render the GUI inventory and place Buttons into place.
 *
 * @param <C> The type of the {@link CustomCache}.
 */
public class GuiUpdate<C extends CustomCache> {

    private final GuiHandler<C> guiHandler;
    private final InventoryAPI<C> inventoryAPI;
    private final WolfyUtilsBukkit wolfyUtilities;
    private final Player player;
    private final Inventory inventory;
    private final Inventory queueInventory;
    private final GuiWindow<C> guiWindow;
    private final GUIHolder<C> guiHolder;

    GuiUpdate(Inventory inventory, GuiHandler<C> guiHandler, GuiWindow<C> guiWindow) {
        this.guiHandler = guiHandler;
        this.inventoryAPI = guiHandler.getInvAPI();
        this.wolfyUtilities = guiHandler.getWolfyUtils();
        this.player = guiHandler.getPlayer();
        this.guiWindow = guiWindow;
        this.queueInventory = Bukkit.createInventory(null, 54, "");
        Class<C> cacheType = guiHandler.getInvAPI().customCacheClass;
        if (inventory != null && inventory.getHolder() instanceof GUIHolder<?> guiHolder && guiHolder.getGuiHandler().getInvAPI().customCacheClass.equals(cacheType)) {
            this.inventory = inventory;
            this.guiHolder = (GUIHolder<C>) guiHolder; // We checked the cache type, so the type is correct
        } else {
            final var holder = new GUIHolder<>(player, guiHandler, guiWindow);
            final var title = guiWindow.updateTitle(holder);
            if (wolfyUtilities.getCore().getCompatibilityManager().isPaper()) {
                // Paper has direct Adventure support, so use it for better titles!
                if (guiWindow.getInventoryType() == null || !guiWindow.getInventoryType().isCreatable()) {
                    this.inventory = Bukkit.createInventory(holder, guiWindow.getSize(), title);
                } else {
                    this.inventory = Bukkit.createInventory(holder, guiWindow.getInventoryType(), title);
                }
            } else {
                if (guiWindow.getInventoryType() == null) {
                    this.inventory = Bukkit.createInventory(holder, guiWindow.getSize(), BukkitComponentSerializer.legacy().serialize(title));
                } else {
                    this.inventory = Bukkit.createInventory(holder, guiWindow.getInventoryType(), BukkitComponentSerializer.legacy().serialize(title));
                }
            }
            holder.setActiveInventory(this.inventory);
            this.guiHolder = holder;
        }
    }

    /**
     * @return The {@link GuiHandler} that caused this update.
     */
    public final GuiHandler<C> getGuiHandler() {
        return guiHolder.getGuiHandler();
    }

    public GUIHolder<C> getGuiHolder() {
        return guiHolder;
    }

    /**
     * @return The player that caused this update.
     */
    public final Player getPlayer() {
        return guiHolder.getPlayer();
    }

    /**
     * @return The {@link Inventory} this update was called from.
     */
    public final Inventory getInventory() {
        return inventory;
    }

    /**
     * Directly set an ItemStack to a slot.
     *
     * @param slot      The slot the item should set in.
     * @param itemStack The ItemStack to set.
     */
    public void setItem(int slot, ItemStack itemStack) {
        queueInventory.setItem(slot, itemStack);
    }

    /**
     * Set an locally registered Button from the current {@link GuiWindow}.
     * <br><br>
     * <strong>It is recommended to save IDs of Buttons as constants in their corresponding {@link GuiWindow} to prevent magic values!</strong>
     *
     * @param slot The slot the Button should be rendered in.
     * @param id   The id of the Button.
     */
    public void setButton(int slot, String id) {
        Button<C> button = guiWindow.getButton(id);
        if (button != null) {
            guiHandler.setButton(guiWindow, slot, id);
            renderButton(button, guiHandler, player, slot);
        }
    }

    /**
     * Set a globally Button registered in a {@link GuiCluster}.
     * <br><br>
     * <strong>It is recommended to save {@link BukkitNamespacedKey}s of Buttons as constants in their corresponding {@link GuiCluster} to prevent magic values!</strong>
     *
     * @param slot          The slot the Button should be rendered in.
     * @param namespacedKey The NamespacedKey of the button. The namespace is the cluster key and the key is the button id.
     */
    public void setButton(int slot, NamespacedKey namespacedKey) {
        Button<C> button = inventoryAPI.getButton(namespacedKey);
        if (button != null) {
            guiHandler.setButton(guiWindow, slot, namespacedKey.toString());
            renderButton(button, guiHandler, player, slot);
        }
    }

    /**
     * Directly render a Button into a specific slot.
     *
     * @param slot   The slot the button should be rendered in.
     * @param button The {@link Button} that should be rendered.
     */
    public void setButton(int slot, @NotNull Button<C> button) {
        if (button != null) {
            guiHandler.setButton(guiWindow, slot, button.getId());
            renderButton(button, guiHandler, player, slot);
        }
    }

    /**
     * Tries to add an Locally registered Button. If it doesn't exist then
     * it will try to get the button globally registered for this GuiCluster.
     *
     * @param slot The slot the button should be rendered in.
     * @param id   The id of the button.
     */
    public void setLocalOrGlobalButton(int slot, String id) {
        Button<C> button = guiWindow.getButton(id);
        if (button == null) {
            button = inventoryAPI.getButton(new BukkitNamespacedKey(guiWindow.getNamespacedKey().getNamespace(), id));
        }
        if (button != null) {
            guiHandler.setButton(guiWindow, slot, id);
            renderButton(button, guiHandler, player, slot);
        }
    }

    private void renderButton(Button<C> button, GuiHandler<C> guiHandler, Player player, int slot) {
        try {
            var itemStack = this.inventory.getItem(slot);
            button.preRender(guiHolder, itemStack, slot);
            button.render(guiHolder, this.queueInventory, slot);
        } catch (IOException e) {
            wolfyUtilities.getConsole().severe("Error while rendering Button \"" + button.getId() + "\"!");
            e.printStackTrace();
        }
    }

    final void applyChanges() {
        if (queueInventory.getContents().length > 0) {
            inventory.setContents(Arrays.copyOfRange(queueInventory.getContents(), 0, inventory.getSize()));
        }
    }

    final void postExecuteButtons(Map<Integer, Button<C>> postExecuteBtns) {
        if (postExecuteBtns != null) {
            postExecuteBtns.forEach((slot, btn) -> {
                try {
                    btn.postExecute(guiHolder, inventory.getItem(slot), slot);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}