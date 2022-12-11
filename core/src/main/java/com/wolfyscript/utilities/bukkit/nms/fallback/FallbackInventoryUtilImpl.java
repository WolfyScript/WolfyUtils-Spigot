package com.wolfyscript.utilities.bukkit.nms.fallback;

import com.wolfyscript.utilities.bukkit.nms.api.InventoryUtil;
import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import com.wolfyscript.utilities.versioning.ServerVersion;
import org.apache.commons.lang3.NotImplementedException;

public class FallbackInventoryUtilImpl extends InventoryUtil {

    public FallbackInventoryUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public void initItemCategories() {
        throw new NotImplementedException("Item categories are not yet implement for " + ServerVersion.getVersion());
    }
}
