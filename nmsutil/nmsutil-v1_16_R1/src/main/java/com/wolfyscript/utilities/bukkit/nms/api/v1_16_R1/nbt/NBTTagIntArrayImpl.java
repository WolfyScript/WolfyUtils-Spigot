package com.wolfyscript.utilities.bukkit.nms.api.v1_16_R1.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagIntArray;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;

public class NBTTagIntArrayImpl extends NBTBaseImpl<net.minecraft.server.v1_16_R1.NBTTagIntArray> implements NBTTagIntArray {

    public static final NBTTagType<NBTTagIntArray> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.INT_ARRAY, nbtBase -> new NBTTagIntArrayImpl((net.minecraft.server.v1_16_R1.NBTTagIntArray) nbtBase));

    NBTTagIntArrayImpl(net.minecraft.server.v1_16_R1.NBTTagIntArray nbtBase) {
        super(nbtBase);
    }

    public static NBTTagIntArray of(int[] array) {
        return new NBTTagIntArrayImpl(new net.minecraft.server.v1_16_R1.NBTTagIntArray(array));
    }

    @Override
    public NBTTagType<NBTTagIntArray> getType() {
        return TYPE;
    }
}
