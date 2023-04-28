package com.wolfyscript.utilities.bukkit.nms.api.v1_16_R2.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagEnd;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;

public class NBTTagEndImpl extends NBTBaseImpl<net.minecraft.server.v1_16_R2.NBTTagEnd> implements NBTTagEnd {

    public static final NBTTagType<NBTTagEnd> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.TAG_END, nbtBase -> new NBTTagEndImpl());

    private NBTTagEndImpl() {
        super(net.minecraft.server.v1_16_R2.NBTTagEnd.b);
    }

    NBTTagEndImpl(net.minecraft.server.v1_16_R2.NBTTagEnd nbtBase) {
        super(net.minecraft.server.v1_16_R2.NBTTagEnd.b);
    }

    public static NBTTagEnd of() {
        return new NBTTagEndImpl();
    }

    @Override
    public NBTTagType<NBTTagEnd> getType() {
        return TYPE;
    }
}