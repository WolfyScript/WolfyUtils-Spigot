package me.wolfyscript.utilities.api.nms.v1_16_R2.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagLong;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;

public class NBTTagLongImpl extends NBTNumberImpl<net.minecraft.server.v1_16_R2.NBTTagLong> implements NBTTagLong {

    public static final NBTTagType<NBTTagLong> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.LONG, nbtBase -> new NBTTagLongImpl((net.minecraft.server.v1_16_R2.NBTTagLong) nbtBase));

    private NBTTagLongImpl() {
        super(net.minecraft.server.v1_16_R2.NBTTagLong.a(0));
    }

    NBTTagLongImpl(net.minecraft.server.v1_16_R2.NBTTagLong nbtBase) {
        super(nbtBase);
    }

    public static NBTTagLong of(long value) {
        return new NBTTagLongImpl(net.minecraft.server.v1_16_R2.NBTTagLong.a(value));
    }

    @Override
    public NBTTagType<NBTTagLong> getType() {
        return TYPE;
    }
}
