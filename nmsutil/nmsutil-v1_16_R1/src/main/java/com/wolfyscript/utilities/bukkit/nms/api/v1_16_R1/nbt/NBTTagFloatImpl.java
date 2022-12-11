package com.wolfyscript.utilities.bukkit.nms.api.v1_16_R1.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagFloat;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;

public class NBTTagFloatImpl extends NBTNumberImpl<net.minecraft.server.v1_16_R1.NBTTagFloat> implements NBTTagFloat {

    public static final NBTTagType<NBTTagFloat> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.FLOAT, nbtBase -> new NBTTagFloatImpl((net.minecraft.server.v1_16_R1.NBTTagFloat) nbtBase));

    private NBTTagFloatImpl() {
        super(net.minecraft.server.v1_16_R1.NBTTagFloat.a(0f));
    }

    NBTTagFloatImpl(net.minecraft.server.v1_16_R1.NBTTagFloat nbtBase) {
        super(nbtBase);
    }

    public static NBTTagFloat of(float value) {
        return new NBTTagFloatImpl(net.minecraft.server.v1_16_R1.NBTTagFloat.a(value));
    }

    @Override
    public NBTTagType<NBTTagFloat> getType() {
        return TYPE;
    }
}
