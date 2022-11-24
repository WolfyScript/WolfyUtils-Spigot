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

import com.wolfyscript.utilities.bukkit.nms.api.block.NMSBrewingStand;
import org.bukkit.block.BrewingStand;

public abstract class BlockUtil extends UtilComponent {

    protected BlockUtil(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    /**
     * Get a more detailed API to edit the NMS BrewingStand values
     *
     * @param brewingStand The Bukkit BrewingStand Block
     * @return The NMSBrewingStand API
     */
    public abstract NMSBrewingStand getNmsBrewingStand(BrewingStand brewingStand);

}
