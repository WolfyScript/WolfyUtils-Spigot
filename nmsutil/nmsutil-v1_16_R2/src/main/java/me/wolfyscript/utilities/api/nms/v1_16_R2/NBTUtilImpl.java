package me.wolfyscript.utilities.api.nms.v1_16_R2;

import com.wolfyscript.utilities.bukkit.nms.api.NBTUtil;
import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import com.wolfyscript.utilities.bukkit.nms.api.nbt.NBTItem;
import me.wolfyscript.utilities.api.nms.v1_16_R2.nbt.NBTItemImpl;
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
