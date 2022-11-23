package me.wolfyscript.utilities.api.nms.v1_16_R1.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagInt;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;

public class NBTTagIntImpl extends NBTNumberImpl<net.minecraft.server.v1_16_R1.NBTTagInt> implements NBTTagInt {

    public static final NBTTagType<NBTTagInt> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.INT, nbtBase -> new NBTTagIntImpl((net.minecraft.server.v1_16_R1.NBTTagInt) nbtBase));

    private NBTTagIntImpl() {
        super(net.minecraft.server.v1_16_R1.NBTTagInt.a(0));
    }

    NBTTagIntImpl(net.minecraft.server.v1_16_R1.NBTTagInt nbtBase) {
        super(nbtBase);
    }

    public static NBTTagInt of(int value) {
        return new NBTTagIntImpl(net.minecraft.server.v1_16_R1.NBTTagInt.a(value));
    }

    @Override
    public NBTTagType<NBTTagInt> getType() {
        return TYPE;
    }
}
