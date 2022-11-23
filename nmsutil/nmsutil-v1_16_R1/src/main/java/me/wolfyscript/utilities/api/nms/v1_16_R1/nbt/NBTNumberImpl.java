package me.wolfyscript.utilities.api.nms.v1_16_R1.nbt;

import net.minecraft.server.v1_16_R1.NBTNumber;

public abstract class NBTNumberImpl<NBT extends NBTNumber> extends NBTBaseImpl<NBT> implements com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTNumber {

    NBTNumberImpl(NBT nbtBase) {
        super(nbtBase);
    }

    @Override
    public long asLong() {
        return nbt.asLong();
    }

    @Override
    public int asInt() {
        return nbt.asInt();
    }

    @Override
    public short asShort() {
        return nbt.asShort();
    }

    @Override
    public byte asByte() {
        return nbt.asByte();
    }

    @Override
    public double asDouble() {
        return nbt.asDouble();
    }

    @Override
    public float asFloat() {
        return nbt.asFloat();
    }
}
