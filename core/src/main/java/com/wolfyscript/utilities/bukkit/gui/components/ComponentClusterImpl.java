package com.wolfyscript.utilities.bukkit.gui.components;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.gui.AbstractBukkitComponent;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.*;
import com.wolfyscript.utilities.common.gui.components.ComponentCluster;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.Optional;
import java.util.Set;

@KeyedStaticId(key = "cluster")
public class ComponentClusterImpl extends AbstractBukkitComponent implements ComponentCluster {

    private final BiMap<String, Component> children;

    public ComponentClusterImpl(String internalID, WolfyUtils wolfyUtils, Component parent, IntList slots) {
        super(internalID, wolfyUtils, parent, slots);
        this.children = HashBiMap.create();
    }

    @Override
    public Set<? extends Component> childComponents() {
        return children.values();
    }

    @Override
    public Optional<? extends Component> getChild(String id) {
        return Optional.ofNullable(children.get(id));
    }

    @Override
    public Renderer getRenderer() {
        return new ComponentClusterRendererImpl();
    }

    @Override
    public Renderer construct(GuiViewManager guiViewManager) {
        return new ComponentClusterRendererImpl();
    }

    @Override
    public int width() {
        return 0;
    }

    @Override
    public int height() {
        return 0;
    }

    @Override
    public void update(GuiViewManager viewManager, GuiHolder guiHolder, RenderContext renderContext) {

    }
}
