package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.GuiAPIManagerCommonImpl;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.RouterBuilder;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class GuiAPIManagerImpl extends GuiAPIManagerCommonImpl {

    public GuiAPIManagerImpl(WolfyUtils wolfyUtils) {
        super(wolfyUtils);
    }

    @Override
    public void registerRouter(String id, Consumer<RouterBuilder> consumer) {
        RouterBuilder builder = new RouterBuilderImpl(id, wolfyUtils, null);
        consumer.accept(builder);
        registerCluster(builder.create(null));
    }

    @Override
    public  GuiViewManager createView(String clusterID, UUID... uuids) {
        return getRouter(clusterID).map(cluster -> new GuiViewManagerImpl(wolfyUtils, cluster, Set.of(uuids))).orElse(null);
    }

}
