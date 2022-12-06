package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagShort;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;

public class NBTTagShortImpl extends NBTNumberImpl<net.minecraft.server.v1_16_R3.NBTTagShort> implements NBTTagShort {

    public static final NBTTagType<NBTTagShort> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.SHORT, nbtBase -> new NBTTagShortImpl((net.minecraft.server.v1_16_R3.NBTTagShort) nbtBase));

    private NBTTagShortImpl() {
        super(net.minecraft.server.v1_16_R3.NBTTagShort.a((short) 0));
    }

    NBTTagShortImpl(net.minecraft.server.v1_16_R3.NBTTagShort nbtBase) {
        super(nbtBase);
    }

    public static NBTTagShort of(short value) {
        return new NBTTagShortImpl(net.minecraft.server.v1_16_R3.NBTTagShort.a(value));
    }

    @Override
    public NBTTagType<NBTTagShort> getType() {
        return TYPE;
    }
}
