package com.wolfyscript.utilities.bukkit.nms.fallback;

import com.wolfyscript.utilities.bukkit.nms.api.InventoryUtil;
import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.apache.commons.lang3.NotImplementedException;

public class FallbackInventoryUtilImpl extends InventoryUtil {

    protected FallbackInventoryUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public void initItemCategories() {
        throw new NotImplementedException("Item categories are not yet implement for " + ServerVersion.getVersion());
    }
}
