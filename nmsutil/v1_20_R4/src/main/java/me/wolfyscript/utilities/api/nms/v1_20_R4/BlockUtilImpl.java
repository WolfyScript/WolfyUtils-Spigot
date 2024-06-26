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

package me.wolfyscript.utilities.api.nms.v1_20_R4;

import me.wolfyscript.utilities.api.nms.BlockUtil;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.v1_20_R4.block.NMSBrewingStand;
import org.bukkit.block.BrewingStand;

@Deprecated(forRemoval = true, since = "4.17")
public class BlockUtilImpl extends BlockUtil {

    BlockUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    @Deprecated(forRemoval = true, since = "4.17")
    public NMSBrewingStand getNmsBrewingStand(BrewingStand brewingStand) {
        return new NMSBrewingStand(brewingStand);
    }
}
