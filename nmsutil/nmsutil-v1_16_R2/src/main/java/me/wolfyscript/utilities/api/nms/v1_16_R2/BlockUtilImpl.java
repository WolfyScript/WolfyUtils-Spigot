package me.wolfyscript.utilities.api.nms.v1_16_R2;

import com.wolfyscript.utilities.bukkit.nms.api.BlockUtil;
import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import com.wolfyscript.utilities.bukkit.nms.api.block.NMSBrewingStand;
import org.bukkit.block.BrewingStand;

public class BlockUtilImpl extends BlockUtil {

    BlockUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public NMSBrewingStand getNmsBrewingStand(BrewingStand brewingStand) {
        return new me.wolfyscript.utilities.api.nms.v1_16_R2.block.NMSBrewingStand(brewingStand);
    }
}
