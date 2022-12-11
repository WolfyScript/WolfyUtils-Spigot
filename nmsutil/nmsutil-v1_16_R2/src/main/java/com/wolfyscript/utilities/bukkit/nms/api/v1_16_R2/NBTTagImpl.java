package com.wolfyscript.utilities.bukkit.nms.api.v1_16_R2;

import com.wolfyscript.utilities.bukkit.nms.api.NBTTag;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.*;
import com.wolfyscript.utilities.bukkit.nms.api.v1_16_R2.nbt.NBTTagByteArrayImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_16_R2.nbt.NBTTagByteImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_16_R2.nbt.NBTTagCompoundImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_16_R2.nbt.NBTTagDoubleImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_16_R2.nbt.NBTTagEndImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_16_R2.nbt.NBTTagFloatImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_16_R2.nbt.NBTTagIntArrayImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_16_R2.nbt.NBTTagIntImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_16_R2.nbt.NBTTagListImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_16_R2.nbt.NBTTagLongArrayImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_16_R2.nbt.NBTTagLongImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_16_R2.nbt.NBTTagShortImpl;
import com.wolfyscript.utilities.bukkit.nms.api.v1_16_R2.nbt.NBTTagStringImpl;
import me.wolfyscript.utilities.api.nms.v1_16_R2.nbt.*;

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
