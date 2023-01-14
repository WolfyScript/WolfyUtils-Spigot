package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Cluster;
import com.wolfyscript.utilities.common.gui.ClusterComponentBuilder;
import com.wolfyscript.utilities.common.gui.Data;
import com.wolfyscript.utilities.common.gui.GuiAPIManagerCommonImpl;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class GuiAPIManagerImpl extends GuiAPIManagerCommonImpl {

    public GuiAPIManagerImpl(WolfyUtils wolfyUtils) {
        super(wolfyUtils);
    }

    @Override
    public <D extends Data> void registerCluster(String id, Class<D> dataType, Consumer<ClusterComponentBuilder<D>> consumer) {
        ClusterComponentBuilder<D> builder = new ClusterImpl.Builder<>(id, null);
        consumer.accept(builder);
        registerCluster(builder.create());
    }

    @Override
    public <D extends Data> GuiViewManager<D> createView(String clusterID, Class<D> dataType, UUID... uuids) {
        Optional<Cluster<D>> clusterOptional = getCluster(clusterID, dataType);
        return clusterOptional.<GuiViewManager<D>>map(cluster -> new GuiViewManagerImpl<>(wolfyUtils, cluster, Set.of(uuids))).orElse(null);
    }
}
