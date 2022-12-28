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

package com.wolfyscript.utilities.bukkit.gui.callback;

import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import com.wolfyscript.utilities.bukkit.gui.GUIHolder;
import com.wolfyscript.utilities.bukkit.gui.button.Button;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import com.wolfyscript.utilities.common.gui.GUIInteractionDetails;
import java.io.IOException;

/**
 * @param <C> The type of the {@link CustomCache}
 */
public interface CallbackButtonAction<C extends CustomCache> {

    /**
     * @param cache      The current cache of the GuiHandler.
     * @param guiHandler The current GuiHandler.
     * @param player     The current Player.
     * @param inventory  The original/previous inventory. No changes to this inventory will be applied on render!
     * @param slot       The slot in which the button is rendered.
     * @param event      The previous event of the click that caused the update. Can be a InventoryClickEvent or InventoryDragEvent
     * @return a boolean indicating whether the interaction should be cancelled. If true the interaction is cancelled.
     * @throws IOException if an error occurs on the execution.
     */
    ButtonInteractionResult run(GUIHolder<C> holder, C cache, Button<C> button, int slot, GUIInteractionDetails details) throws IOException;

}
