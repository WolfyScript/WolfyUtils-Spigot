package me.wolfyscript.utilities.api.nms.v1_16_R3.nbt;

import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagEnd;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTTagType;

public class NBTTagEndImpl extends NBTBaseImpl<net.minecraft.server.v1_16_R3.NBTTagEnd> implements NBTTagEnd {

    public static final NBTTagType<NBTTagEnd> TYPE = new NBTTagTypeImpl<>(NBTTagType.Type.TAG_END, nbtBase -> new NBTTagEndImpl());

    private NBTTagEndImpl() {
        super(net.minecraft.server.v1_16_R3.NBTTagEnd.b);
    }

    NBTTagEndImpl(net.minecraft.server.v1_16_R3.NBTTagEnd nbtBase) {
        super(net.minecraft.server.v1_16_R3.NBTTagEnd.b);
    }

    public static NBTTagEnd of() {
        return new NBTTagEndImpl();
    }

    @Override
    public NBTTagType<NBTTagEnd> getType() {
        return TYPE;
    }
}
