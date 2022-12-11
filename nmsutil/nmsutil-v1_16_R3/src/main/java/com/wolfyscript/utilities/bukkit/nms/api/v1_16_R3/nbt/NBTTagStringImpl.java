package com.wolfyscript.utilities.bukkit.nms.api.v1_16_R3.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagString;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;

public class NBTTagStringImpl extends NBTBaseImpl<net.minecraft.server.v1_16_R3.NBTTagString> implements NBTTagString {

    public static final NBTTagType<NBTTagString> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.STRING, nbtBase -> new NBTTagStringImpl((net.minecraft.server.v1_16_R3.NBTTagString) nbtBase));

    NBTTagStringImpl(net.minecraft.server.v1_16_R3.NBTTagString nbtBase) {
        super(nbtBase);
    }

    public static NBTTagString of(String value) {
        return new NBTTagStringImpl(net.minecraft.server.v1_16_R3.NBTTagString.a(value));
    }

    @Override
    public NBTTagType<NBTTagString> getType() {
        return TYPE;
    }

    @Override
    public String asString() {
        return nbt.asString();
    }

    @Override
    public String toString() {
        return nbt.toString();
    }
}
