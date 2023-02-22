package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.GuiAPIManagerCommonImpl;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.RouterComponentBuilder;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class GuiAPIManagerImpl extends GuiAPIManagerCommonImpl {

    public GuiAPIManagerImpl(WolfyUtils wolfyUtils) {
        super(wolfyUtils);
    }

    @Override
    public void registerCluster(String id, Consumer<RouterComponentBuilder> consumer) {
        RouterComponentBuilder builder = new ClusterImpl.Builder(id, null);
        consumer.accept(builder);
        registerCluster(builder.create());
    }

    @Override
    public  GuiViewManager createView(String clusterID, UUID... uuids) {
        return getCluster(clusterID).map(cluster -> new GuiViewManagerImpl(wolfyUtils, cluster, Set.of(uuids))).orElse(null);
    }
}
