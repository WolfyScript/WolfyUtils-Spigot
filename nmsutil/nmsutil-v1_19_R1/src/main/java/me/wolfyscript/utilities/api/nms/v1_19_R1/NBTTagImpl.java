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

package me.wolfyscript.utilities.api.nms.v1_19_R1;

import com.wolfyscript.utilities.bukkit.nms.api.NBTTag;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTCompound;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagByte;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagByteArray;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagDouble;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagEnd;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagFloat;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagInt;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagIntArray;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagList;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagLong;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagLongArray;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagShort;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagString;
import me.wolfyscript.utilities.api.nms.v1_19_R1.nbt.NBTTagByteArrayImpl;
import me.wolfyscript.utilities.api.nms.v1_19_R1.nbt.NBTTagByteImpl;
import me.wolfyscript.utilities.api.nms.v1_19_R1.nbt.NBTTagCompoundImpl;
import me.wolfyscript.utilities.api.nms.v1_19_R1.nbt.NBTTagDoubleImpl;
import me.wolfyscript.utilities.api.nms.v1_19_R1.nbt.NBTTagEndImpl;
import me.wolfyscript.utilities.api.nms.v1_19_R1.nbt.NBTTagFloatImpl;
import me.wolfyscript.utilities.api.nms.v1_19_R1.nbt.NBTTagIntArrayImpl;
import me.wolfyscript.utilities.api.nms.v1_19_R1.nbt.NBTTagIntImpl;
import me.wolfyscript.utilities.api.nms.v1_19_R1.nbt.NBTTagListImpl;
import me.wolfyscript.utilities.api.nms.v1_19_R1.nbt.NBTTagLongArrayImpl;
import me.wolfyscript.utilities.api.nms.v1_19_R1.nbt.NBTTagLongImpl;
import me.wolfyscript.utilities.api.nms.v1_19_R1.nbt.NBTTagShortImpl;
import me.wolfyscript.utilities.api.nms.v1_19_R1.nbt.NBTTagStringImpl;

public class NBTTagImpl extends NBTTag {

    @Override
    public NBTTagEnd end() {
        return NBTTagEndImpl.of();
    }

    @Override
    public NBTCompound compound() {
        return NBTTagCompoundImpl.of();
    }

    @Override
    public NBTTagList list() {
        return NBTTagListImpl.of();
    }

    @Override
    public NBTTagByte ofByte(byte value) {
        return NBTTagByteImpl.of(value);
    }

    @Override
    public NBTTagByteArray ofByteArray(byte[] value) {
        return NBTTagByteArrayImpl.of(value);
    }

    @Override
    public NBTTagDouble ofDouble(double value) {
        return NBTTagDoubleImpl.of(value);
    }

    @Override
    public NBTTagFloat ofFloat(float value) {
        return NBTTagFloatImpl.of(value);
    }

    @Override
    public NBTTagInt ofInt(int value) {
        return NBTTagIntImpl.of(value);
    }

    @Override
    public NBTTagIntArray ofIntArray(int[] array) {
        return NBTTagIntArrayImpl.of(array);
    }

    @Override
    public NBTTagLong ofLong(long value) {
        return NBTTagLongImpl.of(value);
    }

    @Override
    public NBTTagLongArray ofLongArray(long[] array) {
        return NBTTagLongArrayImpl.of(array);
    }

    @Override
    public NBTTagShort ofShort(short value) {
        return NBTTagShortImpl.of(value);
    }

    @Override
    public NBTTagString ofString(String value) {
        return NBTTagStringImpl.of(value);
    }
}
