package me.wolfyscript.utilities.api.nms.v1_16_R2;

import io.netty.buffer.ByteBuf;
import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import com.wolfyscript.utilities.bukkit.nms.api.NetworkUtil;
import com.wolfyscript.utilities.bukkit.nms.api.network.MCByteBuf;
import me.wolfyscript.utilities.api.nms.v1_16_R2.network.MCByteBufImpl;

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
