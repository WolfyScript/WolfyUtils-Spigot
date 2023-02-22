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

import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import com.wolfyscript.utilities.bukkit.gui.GUIHolder;
import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiHandler;
import com.wolfyscript.utilities.bukkit.gui.GuiWindow;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * This Button acts as a container for Items.
 * It saves the placed in item and can also execute an action on each click.
 *
 * @param  The type of the {@link CustomCache}
 */
public class ButtonItemInput<C extends CustomCache> extends ButtonAction {

    private final Map<GuiHandler, ItemStack> content;

    ButtonItemInput(String id, ButtonState state) {
        super(id, ButtonType.ITEM_SLOT, state);
        this.content = new HashMap<>();
    }

    @Override
    public InteractionResult execute(GUIHolder holder, int slot) throws IOException {
        if (!getType().equals(ButtonType.DUMMY) && getState().getAction() != null) {
            return getState().getAction().run(holder, holder.getGuiHandler().getCustomCache(), this, slot, null); // TODO: Details
        }
        return InteractionResult.cancel(false);
    }

    @Override
    public void postExecute(GUIHolder holder, ItemStack itemStack, int slot) throws IOException {
        content.put(holder.getGuiHandler(), itemStack != null ? itemStack.clone() : new ItemStack(Material.AIR));
        super.postExecute(holder, itemStack, slot);
    }

    @Override
    public void render(GUIHolder holder, Inventory queueInventory, int slot) throws IOException {
        ItemStack item = getContent(holder.getGuiHandler());
        if (getState().getRenderAction() != null) {
            item = getState().getRenderAction().run(holder, holder.getGuiHandler().getCustomCache(), this, slot, item).getCustomStack().orElse(item);
        }
        queueInventory.setItem(slot, item);
    }

    public ItemStack getContent(GuiHandler guiHandler) {
        return content.computeIfAbsent(guiHandler, g -> new ItemStack(Material.AIR));
    }

    public static class Builder<C extends CustomCache> extends AbstractBuilder<C, ButtonItemInput, Builder> {

        public Builder(GuiWindow window, String id) {
            super(window, id, (Class<ButtonItemInput>) (Object) ButtonItemInput.class);
        }

        public Builder(GuiCluster cluster, String id) {
            super(cluster, id, (Class<ButtonItemInput>) (Object) ButtonItemInput.class);
        }

        @Override
        public ButtonItemInput create() {
            return new ButtonItemInput<>(key, stateBuilder.create());
        }
    }
}
