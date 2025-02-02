package com.wolfyscript.utilities.bukkit.nms.fallback;

import me.wolfyscript.utilities.api.nms.InventoryUtil;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.util.inventory.CreativeModeTab;
import org.bukkit.Material;

public class FallbackInventoryUtilImpl extends InventoryUtil {

    public FallbackInventoryUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public void initItemCategories() {
        // Since 1.19.3 the server has no info about the Creative menu categories anymore!
        // Future versions might sort the items manually, but maybe not...
        for (Material material : Material.values()) {
            CreativeModeTab.of("building_blocks").ifPresent(creativeModeTab -> creativeModeTab.registerMaterial(material));
        }
    }
}
