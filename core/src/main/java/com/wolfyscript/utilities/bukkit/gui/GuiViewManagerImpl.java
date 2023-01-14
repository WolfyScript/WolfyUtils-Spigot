package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Cluster;
import com.wolfyscript.utilities.common.gui.Data;
import com.wolfyscript.utilities.common.gui.GuiViewManagerCommonImpl;
import java.util.Set;
import java.util.UUID;

public class GuiViewManagerImpl<D extends Data> extends GuiViewManagerCommonImpl<D> {

    protected GuiViewManagerImpl(WolfyUtils wolfyUtils, Cluster<D> rootCluster, Set<UUID> viewers) {
        super(wolfyUtils, rootCluster, viewers);
    }

}
