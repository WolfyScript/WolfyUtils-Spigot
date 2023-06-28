package com.wolfyscript.utilities.bukkit.gui.components;

import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.gui.AbstractBukkitComponent;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.Renderer;
import com.wolfyscript.utilities.common.gui.components.ComponentCluster;

import java.util.Optional;
import java.util.Set;

@KeyedStaticId(key = "cluster")
public class ComponentClusterImpl extends AbstractBukkitComponent implements ComponentCluster {

    public ComponentClusterImpl(String internalID, WolfyUtils wolfyUtils, Component parent) {
        super(internalID, wolfyUtils, parent);
    }

    @Override
    public Set<? extends Component> childComponents() {
        return null;
    }

    @Override
    public Optional<? extends Component> getChild(String id) {
        return Optional.empty();
    }

    @Override
    public Renderer<? extends ComponentState> getRenderer() {
        return null;
    }

    @Override
    public int width() {
        return 0;
    }

    @Override
    public int height() {
        return 0;
    }
}
