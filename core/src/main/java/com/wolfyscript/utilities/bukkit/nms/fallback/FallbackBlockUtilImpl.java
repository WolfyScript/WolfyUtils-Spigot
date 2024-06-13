package com.wolfyscript.utilities.bukkit.nms.fallback;

import me.wolfyscript.utilities.api.nms.BlockUtil;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.api.nms.block.NMSBrewingStand;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.block.BrewingStand;

public class FallbackBlockUtilImpl extends BlockUtil {

    FallbackBlockUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public NMSBrewingStand getNmsBrewingStand(BrewingStand brewingStand) {
        throw new NotImplementedException("NMS Brewing Stand is not yet implement for " + ServerVersion.getVersion());
    }
}
