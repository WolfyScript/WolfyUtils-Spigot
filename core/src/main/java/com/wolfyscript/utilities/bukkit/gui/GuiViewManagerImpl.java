package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Cluster;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.Data;
import com.wolfyscript.utilities.common.gui.GuiViewManagerCommonImpl;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class GuiViewManagerImpl<D extends Data> extends GuiViewManagerCommonImpl<D> {

    private ComponentStateNode<D> rootStateNode;
    private Map<Integer, ComponentStateNode<D>> tailStateNodes = new HashMap<>();

    protected GuiViewManagerImpl(WolfyUtils wolfyUtils, Cluster<D> rootCluster, Set<UUID> viewers) {
        super(wolfyUtils, rootCluster, viewers);

    }

    public ComponentStateNode<D> getRootStateNode() {
        return rootStateNode;
    }

    public void setState(Component<D> component, ComponentState<D> state) {

    }

    Optional<ComponentStateNode<D>> getTailNode(int slot) {
        return Optional.ofNullable(tailStateNodes.get(slot));
    }
}
