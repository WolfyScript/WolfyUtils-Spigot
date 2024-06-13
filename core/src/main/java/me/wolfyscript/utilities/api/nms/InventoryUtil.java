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

package me.wolfyscript.utilities.api.nms;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.api.nms.inventory.InjectGUIInventory;
import me.wolfyscript.utilities.util.inventory.CreativeModeTab;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

@Deprecated(forRemoval = true, since = "4.17")
public abstract class InventoryUtil extends UtilComponent {

    protected InventoryUtil(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    /**
     * @deprecated Does not work on Paper and may break on Spigot in later versions than 1.20.4
     */
    @Deprecated
    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryType type) {
        Inventory inventory = Bukkit.createInventory(null, type);
        return InjectGUIInventory.patchInventory(guiHandler, window, inventory, null);
    }

    /**
     * @deprecated Does not work on Paper and may break on Spigot in later versions than 1.20.4
     */
    @Deprecated
    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryType type, String title) {
        Inventory inventory = Bukkit.createInventory(null, type, title);
        return InjectGUIInventory.patchInventory(guiHandler, window, inventory, title);
    }

    /**
     * @deprecated Does not work on Paper and may break on Spigot in later versions than 1.20.4
     */
    @Deprecated
    public <C extends CustomCache> GUIInventory<C> createGUIInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, int size) {
        Inventory inventory = Bukkit.createInventory(null, size);
        return InjectGUIInventory.patchInventory(guiHandler, window, inventory, null);
    }

    /**
     * @deprecated Does not work on Paper and may break on Spigot in later versions than 1.20.4
     */
    @Deprecated
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
