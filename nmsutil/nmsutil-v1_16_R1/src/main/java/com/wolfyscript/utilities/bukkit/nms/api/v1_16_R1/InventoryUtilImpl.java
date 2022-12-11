package com.wolfyscript.utilities.bukkit.nms.api.v1_16_R1;

import com.wolfyscript.utilities.bukkit.nms.api.InventoryUtil;
import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import com.wolfyscript.utilities.bukkit.world.inventory.CreativeModeTab;
import net.minecraft.server.v1_16_R1.Item;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R1.util.CraftMagicNumbers;

public class InventoryUtilImpl extends InventoryUtil {

    protected InventoryUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public void initItemCategories() {
        for (Material material : Material.values()) {
            if (material.isLegacy()) continue;
            Item item = CraftMagicNumbers.getItem(material);
            if (item != null) {
                net.minecraft.server.v1_16_R1.CreativeModeTab creativeModeTab = item.q();
                if (creativeModeTab != null) {
                    CreativeModeTab.of(creativeModeTab.c()).ifPresent(tab-> tab.registerMaterial(material));
                }
            }

        }
    }


}
