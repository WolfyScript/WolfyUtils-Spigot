package me.wolfyscript.utilities.api.nms.v1_16_R1.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagDouble;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;

public class NBTTagDoubleImpl extends NBTNumberImpl<net.minecraft.server.v1_16_R1.NBTTagDouble> implements NBTTagDouble {

    public static final NBTTagType<NBTTagDouble> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.DOUBLE, nbtBase -> new NBTTagDoubleImpl((net.minecraft.server.v1_16_R1.NBTTagDouble) nbtBase));

    private NBTTagDoubleImpl() {
        super(net.minecraft.server.v1_16_R1.NBTTagDouble.a(0d));
    }

    NBTTagDoubleImpl(net.minecraft.server.v1_16_R1.NBTTagDouble nbtBase) {
        super(nbtBase);
    }

    public static NBTTagDouble of(double value) {
        return new NBTTagDoubleImpl(net.minecraft.server.v1_16_R1.NBTTagDouble.a(value));
    }

    @Override
    public NBTTagType<NBTTagDouble> getType() {
        return TYPE;
    }
}
