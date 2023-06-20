package com.wolfyscript.utilities.bukkit.nms.api.v1_16_R3;

import com.wolfyscript.utilities.bukkit.nms.api.v1_16_R3.network.MCByteBufImpl;
import io.netty.buffer.ByteBuf;
import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import com.wolfyscript.utilities.bukkit.nms.api.NetworkUtil;
import com.wolfyscript.utilities.bukkit.nms.api.network.MCByteBuf;

public class NetworkUtilImpl extends NetworkUtil {

    protected NetworkUtilImpl(NMSUtil nmsUtil) {
        super(nmsUtil);
    }

    @Override
    public MCByteBuf buffer(ByteBuf byteBuf) {
        return new MCByteBufImpl(byteBuf);
    }

    @Override
    public MCByteBuf buffer() {
        return new MCByteBufImpl();
    }
}
