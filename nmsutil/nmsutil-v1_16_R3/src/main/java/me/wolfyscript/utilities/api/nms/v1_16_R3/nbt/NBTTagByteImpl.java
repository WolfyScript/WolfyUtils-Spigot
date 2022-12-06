package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagByte;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;

public class NBTTagByteImpl extends NBTNumberImpl<net.minecraft.server.v1_16_R3.NBTTagByte> implements NBTTagByte {

    public static final NBTTagType<NBTTagByte> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.BYTE, nbtBase -> new NBTTagByteImpl((net.minecraft.server.v1_16_R3.NBTTagByte) nbtBase));

    private NBTTagByteImpl() {
        super(net.minecraft.server.v1_16_R3.NBTTagByte.b);
    }

    NBTTagByteImpl(net.minecraft.server.v1_16_R3.NBTTagByte nbtBase) {
        super(nbtBase);
    }

    public static NBTTagByte of(byte value) {
        return new NBTTagByteImpl(net.minecraft.server.v1_16_R3.NBTTagByte.a(value));
    }

    @Override
    public NBTTagType<NBTTagByte> getType() {
        return TYPE;
    }
}
