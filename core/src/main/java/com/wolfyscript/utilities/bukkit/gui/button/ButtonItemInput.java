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

package com.wolfyscript.utilities.bukkit.gui.button;

import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiHandler;
import com.wolfyscript.utilities.bukkit.gui.GuiWindow;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.wolfyscript.utilities.bukkit.nms.api.inventory.GUIInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * This Button acts as a container for Items.
 * It saves the placed in item and can also execute an action on each click.
 *
 * @param <C> The type of the {@link CustomCache}
 */
public class ButtonItemInput<C extends CustomCache> extends ButtonAction<C> {

    private final Map<GuiHandler<C>, ItemStack> content;

    ButtonItemInput(String id, ButtonState<C> state) {
        super(id, ButtonType.ITEM_SLOT, state);
        this.content = new HashMap<>();
    }

    @Override
    public boolean execute(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, int slot, InventoryInteractEvent event) throws IOException {
        if (!getType().equals(ButtonType.DUMMY) && getState().getAction() != null) {
            return getState().getAction().run(guiHandler.getCustomCache(), guiHandler, player, inventory, this, slot, event);
        }
        return false;
    }

    @Override
    public void postExecute(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, ItemStack itemStack, int slot, InventoryInteractEvent event) throws IOException {
        content.put(guiHandler, itemStack != null ? itemStack.clone() : new ItemStack(Material.AIR));
        super.postExecute(guiHandler, player, inventory, itemStack, slot, event);
    }

    @Override
    public void render(GuiHandler<C> guiHandler, Player player, GUIInventory<C> guiInventory, Inventory inventory, ItemStack itemStack, int slot, boolean help) throws IOException {
        ItemStack item = getContent(guiHandler);
        if (getState().getRenderAction() != null) {
            item = getState().getRenderAction().render(guiHandler.getCustomCache(), guiHandler, player, guiInventory, item, slot).getCustomStack().orElse(item);
        }
        inventory.setItem(slot, item);
    }

    public ItemStack getContent(GuiHandler<C> guiHandler) {
        return content.computeIfAbsent(guiHandler, g -> new ItemStack(Material.AIR));
    }

    public static class Builder<C extends CustomCache> extends AbstractBuilder<C, ButtonItemInput<C>, Builder<C>> {

        public Builder(GuiWindow<C> window, String id) {
            super(window, id, (Class<ButtonItemInput<C>>) (Object) ButtonItemInput.class);
        }

        public Builder(GuiCluster<C> cluster, String id) {
            super(cluster, id, (Class<ButtonItemInput<C>>) (Object) ButtonItemInput.class);
        }

        @Override
        public ButtonItemInput<C> create() {
            return new ButtonItemInput<>(key, stateBuilder.create());
        }
    }
}
