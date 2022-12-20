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
import com.wolfyscript.utilities.bukkit.nms.api.inventory.GUIInventory;
import java.io.IOException;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;

/**
 * This Button acts as a dummy, it will not run the action, even if you set one for the ButtonState!
 *
 * @param <C> The type of the {@link CustomCache}
 */
public class ButtonDummy<C extends CustomCache> extends ButtonAction<C> {

    ButtonDummy(String id, ButtonState<C> state) {
        super(id, ButtonType.DUMMY, state);
    }

    public boolean execute(GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, int slot, InventoryInteractEvent event) throws IOException {
        return true; // This is a dummy button. Always cancel the interaction!
    }

    public static class Builder<C extends CustomCache> extends AbstractBuilder<C, ButtonDummy<C>, Builder<C>> {

        public Builder(GuiWindow<C> window, String id) {
            super(window, id, (Class<ButtonDummy<C>>) (Object) ButtonDummy.class);
        }

        public Builder(GuiCluster<C> cluster, String id) {
            super(cluster, id, (Class<ButtonDummy<C>>) (Object) ButtonDummy.class);
        }

        @Override
        public ButtonDummy<C> create() {
            return new ButtonDummy<>(key, stateBuilder.create());
        }
    }

}
