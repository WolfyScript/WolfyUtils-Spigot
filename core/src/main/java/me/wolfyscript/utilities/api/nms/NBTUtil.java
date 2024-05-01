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

import me.wolfyscript.utilities.api.nms.nbt.NBTItem;
import org.apache.commons.lang.NotImplementedException;

@Deprecated(since = "4.16.2.0", forRemoval = true)
public abstract class NBTUtil extends UtilComponent {

    protected NBTTag nbtTag;

    protected NBTUtil(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    /**
     * The NBTTag has many methods to create specific Tags.
     *
     * @return The instance of the NBTTag builder.
     */
    public NBTTag getTag() {
        throw new NotImplementedException("This API is no longer available in 1.20.5+");
    }

    /**
     * Get an instance of the NBTItem interface, which allows you to read and edit the internal NBT Tags of the item.
     * It provides a fully featured NBT API so no need for NMS or Reflection of any kind.
     *
     * @param bukkitItemStack The bukkit ItemStack
     * @return The instance of the NBTItem interface containing an API for NBT Tags
     */
    public NBTItem getItem(org.bukkit.inventory.ItemStack bukkitItemStack) {
        throw new NotImplementedException("This API is no longer available in 1.20.5+. Use the Item-NBT-API by tr7zw instead!");
    }

    public NBTItem getDirectItem(org.bukkit.inventory.ItemStack bukkitItemStack) {
        throw new NotImplementedException("This API is no longer available in 1.20.5+. Use the Item-NBT-API by tr7zw instead!");
    }

}
