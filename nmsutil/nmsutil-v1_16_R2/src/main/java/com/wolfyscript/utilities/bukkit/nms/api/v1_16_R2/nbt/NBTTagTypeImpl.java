package com.wolfyscript.utilities.bukkit.nms.api.v1_16_R2.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTBase;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class NBTTagTypeImpl<T extends NBTBase> implements NBTTagType<T> {

    protected final Type type;
    private final Function<Object, T> function;

    public NBTTagTypeImpl(Type type, Function<Object, T> nbtBase){
        this.type = type;
        this.function = nbtBase;
    }

    public T get(@Nullable Object nbtBase){
        if(nbtBase instanceof net.minecraft.server.v1_16_R2.NBTBase && type.is(((net.minecraft.server.v1_16_R2.NBTBase) nbtBase).getTypeId())){
            return function.apply(nbtBase);
        }
        return null;
    }

    @Override
    public Type getType() {
        return type;
    }

}
