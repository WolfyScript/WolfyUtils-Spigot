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

package com.wolfyscript.utilities.bukkit.gui.button.buttons;

import com.wolfyscript.utilities.bukkit.gui.GuiCluster;
import com.wolfyscript.utilities.bukkit.gui.GuiHandler;
import com.wolfyscript.utilities.bukkit.gui.GuiWindow;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonRender;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonState;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonType;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import java.io.IOException;
import com.wolfyscript.utilities.bukkit.nms.api.inventory.GUIInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This Button acts as a dummy, it will not run the action, even if you set one for the ButtonState!
 *
 * @param <C> The type of the {@link CustomCache}
 */
public class DummyButton<C extends CustomCache> extends ActionButton<C> {

    public DummyButton(String id, ButtonState<C> state) {
        super(id, ButtonType.DUMMY, state);
    }

    public DummyButton(String id) {
        super(id, ButtonType.DUMMY, null);
    }

    public DummyButton(String id, ItemStack itemStack) {
        this(id, new ButtonState<>(id, itemStack));
    }

    public DummyButton(String id, Material material) {
        this(id, new ButtonState<>(id, material));
    }

    public DummyButton(String id, ItemStack itemStack, ButtonRender<C> render) {
        this(id, new ButtonState<>(id, itemStack, render));
    }

    public DummyButton(String id, Material material, ButtonRender<C> render) {
        this(id, new ButtonState<>(id, material, render));
    }

    public boolean execute(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, int slot, InventoryInteractEvent event) throws IOException {
        //NOTHING
        return true;
    }

    public static class Builder<C extends CustomCache> extends AbstractBuilder<C, DummyButton<C>, Builder<C>> {

        public Builder(GuiWindow<C> window, String id) {
            super(window, id, (Class<DummyButton<C>>) (Object) DummyButton.class);
        }

        public Builder(GuiCluster<C> cluster, String id) {
            super(cluster, id, (Class<DummyButton<C>>) (Object) DummyButton.class);
        }

        @Override
        public DummyButton<C> create() {
            return new DummyButton<>(key, stateBuilder.create());
        }
    }

}
