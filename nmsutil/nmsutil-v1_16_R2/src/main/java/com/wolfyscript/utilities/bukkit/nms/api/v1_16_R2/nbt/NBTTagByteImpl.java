package com.wolfyscript.utilities.bukkit.nms.api.v1_16_R2.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagByte;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;

public class NBTTagByteImpl extends NBTNumberImpl<net.minecraft.server.v1_16_R2.NBTTagByte> implements NBTTagByte {

    public static final NBTTagType<NBTTagByte> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.BYTE, nbtBase -> new NBTTagByteImpl((net.minecraft.server.v1_16_R2.NBTTagByte) nbtBase));

    private NBTTagByteImpl() {
        super(net.minecraft.server.v1_16_R2.NBTTagByte.b);
    }

    NBTTagByteImpl(net.minecraft.server.v1_16_R2.NBTTagByte nbtBase) {
        super(nbtBase);
    }

    public static NBTTagByte of(byte value) {
        return new NBTTagByteImpl(net.minecraft.server.v1_16_R2.NBTTagByte.a(value));
    }

    @Override
    public NBTTagType<NBTTagByte> getType() {
        return TYPE;
    }
}
