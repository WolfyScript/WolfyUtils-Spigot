package me.wolfyscript.utilities.api.nms.v1_17_R1;

import me.wolfyscript.utilities.api.nms.InventoryUtil;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.util.inventory.CreativeModeTab;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;

public class InventoryUtilImpl extends InventoryUtil {

    protected InventoryUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public final void initItemCategories() {
        for (Material material : Material.values()) {
            if (material.isLegacy()) continue;
            var item = CraftMagicNumbers.getItem(material);
            if (item != null) {
                var creativeModeTab = item.getItemCategory();
                if (creativeModeTab != null) {
                    CreativeModeTab.of(creativeModeTab.getRecipeFolderName()).ifPresent(tab-> tab.registerMaterial(material));
                }
            }
        }
    }
}
