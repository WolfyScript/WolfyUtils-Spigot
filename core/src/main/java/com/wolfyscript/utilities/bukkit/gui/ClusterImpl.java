package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.collect.BiMap;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Cluster;
import com.wolfyscript.utilities.common.gui.ClusterCommonImpl;
import com.wolfyscript.utilities.common.gui.ClusterComponentBuilder;
import com.wolfyscript.utilities.common.gui.ClusterStateBuilder;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.Data;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.MenuComponent;
import com.wolfyscript.utilities.common.gui.StateSelector;
import com.wolfyscript.utilities.common.gui.WindowComponentBuilder;
import java.util.UUID;
import java.util.function.Consumer;

public class ClusterImpl<D extends Data> extends ClusterCommonImpl<D> {

    protected ClusterImpl(String id, Class<D> dataType, WolfyUtils wolfyUtils, Cluster<D> parent, StateSelector<D> stateSelector, ComponentState<D>[] states, BiMap<String, ? extends MenuComponent<D>> children, MenuComponent<D> entry) {
        super(id, dataType, wolfyUtils, parent, stateSelector, states, children, entry);
    }

    public static class Builder<D extends Data> extends ClusterCommonImpl.Builder<D> {

        protected Builder(String subID, Cluster<D> parent) {
            super(subID, parent, new ChildBuilder<>(parent));
        }

        @Override
        public ClusterComponentBuilder<D> state(Consumer<ClusterStateBuilder<D>> consumer) {
            return null;
        }

        @Override
        protected Cluster<D> constructImplementation(String id, Class<D> dataType, WolfyUtils wolfyUtils, Cluster<D> cluster, StateSelector<D> stateSelector, ComponentState<D>[] componentStates, BiMap<String, ? extends MenuComponent<D>> children, MenuComponent<D> menuComponent) {
            return new ClusterImpl<>(id, dataType, wolfyUtils, cluster, stateSelector, componentStates, children, menuComponent);
        }
    }

    public static class ChildBuilder<D extends Data> extends ClusterCommonImpl.ChildBuilder<D> {

        protected ChildBuilder(Cluster<D> parent) {
            super(parent);
        }

        @Override
        protected ClusterComponentBuilder<D> constructClusterBuilderImpl(String id, Cluster<D> cluster) {
            return new ClusterImpl.Builder<>(id, cluster);
        }

        @Override
        protected WindowComponentBuilder<D> constructWindowBuilderImpl(String id, Cluster<D> cluster) {
            return new WindowImpl.BuilderImpl<>(id, cluster);
        }
    }

}
