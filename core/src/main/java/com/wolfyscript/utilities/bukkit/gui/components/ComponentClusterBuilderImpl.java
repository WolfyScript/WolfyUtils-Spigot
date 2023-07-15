package com.wolfyscript.utilities.bukkit.gui.components;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.gui.AbstractBukkitComponentBuilder;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentBuilder;
import com.wolfyscript.utilities.common.gui.ComponentBuilderSettings;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.components.ComponentCluster;
import com.wolfyscript.utilities.common.gui.components.ComponentClusterBuilder;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.function.Consumer;

@KeyedStaticId(key = "cluster")
@ComponentBuilderSettings(base = ComponentClusterBuilder.class, component = ComponentCluster.class)
public class ComponentClusterBuilderImpl extends AbstractBukkitComponentBuilder<ComponentCluster, Component> implements ComponentClusterBuilder {

    @JsonCreator
    public ComponentClusterBuilderImpl(@JsonProperty("id") String id, @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils, @JsonProperty("slots") int[] slots) {
        super(id, wolfyUtils, IntList.of(slots));
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> ComponentClusterBuilder render(String s, Class<B> aClass, Consumer<B> consumer) {
        return null;
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> ComponentClusterBuilder renderAt(int i, String s, Class<B> aClass, Consumer<B> consumer) {
        return null;
    }

    public ComponentClusterImpl create(Component state, GuiViewManager viewManager) {
        return new ComponentClusterImpl(getID(), getWolfyUtils(), state, getSlots());
    }

    @Override
    public ComponentClusterImpl create(Component component) {
        return null;
    }
}
