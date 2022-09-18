package me.wolfyscript.utilities.api.nms.v1_16_R2;

import me.wolfyscript.utilities.api.nms.InventoryUtil;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.util.inventory.CreativeModeTab;
import net.minecraft.server.v1_16_R2.Item;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R2.util.CraftMagicNumbers;

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
                net.minecraft.server.v1_16_R2.CreativeModeTab creativeModeTab = item.q();
                if (creativeModeTab != null) {
                    CreativeModeTab.of(creativeModeTab.b()).ifPresent(tab-> tab.registerMaterial(material));
                }
            }

        }
    }


}
