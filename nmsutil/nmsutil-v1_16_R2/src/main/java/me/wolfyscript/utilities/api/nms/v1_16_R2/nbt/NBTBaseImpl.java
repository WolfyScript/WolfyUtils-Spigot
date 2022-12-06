package me.wolfyscript.utilities.api.nms.v1_16_R2.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTBase;

public abstract class NBTBaseImpl<NBT extends net.minecraft.server.v1_16_R2.NBTBase> implements NBTBase {

    protected final NBT nbt;

    NBTBaseImpl(NBT nbtBase) {
        this.nbt = nbtBase;
    }

    @Override
    public byte getTypeId() {
        return nbt.getTypeId();
    }

    @Override
    public String toString() {
        return nbt.toString();
    }

    public NBT getNbt() {
        return nbt;
    }
}
