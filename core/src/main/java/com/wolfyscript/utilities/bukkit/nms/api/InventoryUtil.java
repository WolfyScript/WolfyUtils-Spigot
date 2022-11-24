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

package com.wolfyscript.utilities.bukkit.nms.api;

import com.wolfyscript.utilities.bukkit.gui.GuiHandler;
import com.wolfyscript.utilities.bukkit.gui.GuiWindow;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import com.wolfyscript.utilities.bukkit.nms.api.inventory.GUIInventory;
import com.wolfyscript.utilities.bukkit.nms.api.inventory.InjectGUIInventory;
import com.wolfyscript.utilities.bukkit.world.inventory.CreativeModeTab;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public abstract class InventoryUtil extends UtilComponent {

    protected InventoryUtil(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryType type) {
        Inventory inventory = Bukkit.createInventory(null, type);
        return InjectGUIInventory.patchInventory(guiHandler, window, inventory, null);
    }

    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryType type, String title) {
        Inventory inventory = Bukkit.createInventory(null, type, title);
        return InjectGUIInventory.patchInventory(guiHandler, window, inventory, title);
    }

    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, int size) {
        Inventory inventory = Bukkit.createInventory(null, size);
        return InjectGUIInventory.patchInventory(guiHandler, window, inventory, null);
    }

    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, int size, String title) {
        Inventory inventory = Bukkit.createInventory(null, size, title);
        return InjectGUIInventory.patchInventory(guiHandler, window, inventory, title);
    }

    /**
     * This is used for internal initialization of the {@link CreativeModeTab} registry.
     *
     * @deprecated Used for internal initialization. Has no effect if called a second time!
     */
    @Deprecated
    public abstract void initItemCategories();
}
