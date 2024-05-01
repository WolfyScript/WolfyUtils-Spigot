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

import com.wolfyscript.utilities.bukkit.nms.fallback.FallbackInventoryUtilImpl;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.nms.ItemUtil;
import me.wolfyscript.utilities.api.nms.NBTUtil;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.NetworkUtil;
import org.bukkit.plugin.Plugin;

public class NMSEntry extends NMSUtil {

    /**
     * The class that implements this NMSUtil needs to have a constructor with just the {@link Plugin} parameter.
     *
     * @param wolfyUtilities
     */
    public NMSEntry(WolfyUtilities wolfyUtilities) {
        super(wolfyUtilities);
        this.blockUtil = new BlockUtilImpl(this);
        this.itemUtil = new ItemUtil(this) { };
        this.inventoryUtil = new FallbackInventoryUtilImpl(this);
        this.nbtUtil = new NBTUtil(this) { };
        this.recipeUtil = new RecipeUtilImpl(this);
        this.networkUtil = new NetworkUtil(this) { };
    }

}
