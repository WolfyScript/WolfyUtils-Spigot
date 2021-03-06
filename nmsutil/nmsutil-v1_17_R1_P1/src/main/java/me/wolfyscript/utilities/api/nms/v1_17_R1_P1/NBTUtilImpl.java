package me.wolfyscript.utilities.api.nms.v1_17_R1_P1;

import me.wolfyscript.utilities.api.nms.NBTUtil;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.nbt.NBTItem;
import me.wolfyscript.utilities.api.nms.v1_17_R1_P1.nbt.NBTItemImpl;
import org.bukkit.inventory.ItemStack;

public class NBTUtilImpl extends NBTUtil {

    protected NBTUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
        this.nbtTag = new NBTTagImpl();
    }

    @Override
    public NBTItem getItem(org.bukkit.inventory.ItemStack bukkitItemStack) {
        return new NBTItemImpl(bukkitItemStack, false);
    }

    @Override
    public NBTItem getDirectItem(ItemStack bukkitItemStack) {
        return new NBTItemImpl(bukkitItemStack, true);
    }

}
