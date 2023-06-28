package com.wolfyscript.utilities.bukkit.gui.components;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.gui.AbstractBukkitComponentBuilder;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentBuilderSettings;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.components.ComponentCluster;
import com.wolfyscript.utilities.common.gui.components.ComponentClusterBuilder;

import java.util.List;

@KeyedStaticId(key = "cluster")
@ComponentBuilderSettings(base = ComponentClusterBuilder.class, component = ComponentCluster.class)
public class ComponentClusterBuilderImpl extends AbstractBukkitComponentBuilder<ComponentClusterImpl, ComponentState> {

    @JsonCreator
    public ComponentClusterBuilderImpl(@JsonProperty("id") String id, @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils, @JsonProperty("slots") List<Integer> slots) {
        super(id, wolfyUtils, slots);
    }

    @Override
    public ComponentClusterImpl create(ComponentState state) {
        return null;
    }
}
