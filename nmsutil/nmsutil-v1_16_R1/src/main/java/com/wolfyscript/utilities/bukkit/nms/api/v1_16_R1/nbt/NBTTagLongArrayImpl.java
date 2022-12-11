package com.wolfyscript.utilities.bukkit.nms.api.v1_16_R1.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagLongArray;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;

public class NBTTagLongArrayImpl extends NBTBaseImpl<net.minecraft.server.v1_16_R1.NBTTagLongArray> implements NBTTagLongArray {

    public static final NBTTagType<NBTTagLongArray> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.LONG_ARRAY, nbtBase -> new NBTTagLongArrayImpl((net.minecraft.server.v1_16_R1.NBTTagLongArray) nbtBase));

    NBTTagLongArrayImpl(net.minecraft.server.v1_16_R1.NBTTagLongArray nbtBase) {
        super(nbtBase);
    }

    public static NBTTagLongArray of(long[] array) {
        return new NBTTagLongArrayImpl(new net.minecraft.server.v1_16_R1.NBTTagLongArray(array));
    }

    @Override
    public NBTTagType<NBTTagLongArray> getType() {
        return TYPE;
    }
}
