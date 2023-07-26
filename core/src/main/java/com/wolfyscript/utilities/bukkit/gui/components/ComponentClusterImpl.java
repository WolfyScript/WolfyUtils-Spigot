package com.wolfyscript.utilities.bukkit.gui.components;

import com.google.common.collect.Multimap;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.gui.GuiViewManagerImpl;
import com.wolfyscript.utilities.bukkit.gui.RenderContextImpl;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.*;
import com.wolfyscript.utilities.common.gui.components.ComponentCluster;
import com.wolfyscript.utilities.common.gui.impl.AbstractComponentImpl;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@KeyedStaticId(key = "cluster")
public class ComponentClusterImpl extends AbstractComponentImpl implements ComponentCluster {

    private int width;
    private int height;
    private final Multimap<Component, Integer> children;

    public ComponentClusterImpl(String internalID, WolfyUtils wolfyUtils, Component parent, IntList slots, Multimap<Component, Integer> children) {
        super(internalID, wolfyUtils, parent, slots);
        this.children = children;

        int topLeft = 54;
        int bottomRight = 0;

        for (int slot : this.children.values()) {
            topLeft = Math.min(slot, topLeft);
            bottomRight = Math.max(slot, bottomRight);
        }
        this.width = Math.abs((topLeft % 9) - (bottomRight % 9)) + 1;
        this.height = Math.abs((topLeft / 9) - (bottomRight / 9)) + 1;
    }

    public ComponentClusterImpl(ComponentClusterImpl staticComponent) {
        super(staticComponent.getID(), staticComponent.getWolfyUtils(), staticComponent.parent(), staticComponent.getSlots());
        this.children = staticComponent.children;

    }

    @Override
    public Set<? extends Component> childComponents() {
        return children.keySet();
    }

    @Override
    public Optional<? extends Component> getChild(String id) {
        return Optional.empty();
    }

    @Override
    public ComponentCluster construct(GuiViewManager guiViewManager) {
        return this;
    }

    @Override
    public void remove(GuiHolder guiHolder, GuiViewManager guiViewManager, RenderContext renderContext) {
        for (Component component : children.keySet()) {
            component.remove(guiHolder, guiViewManager, renderContext);
        }
    }

    @Override
    public void update(GuiViewManager viewManager, GuiHolder guiHolder, RenderContext context) {
        if (!(context instanceof RenderContextImpl renderContext)) return;

        for (Component component : children.keySet()) {
            for (Integer slot : component.getSlots()) {
                renderContext.setSlotOffsetToParent(slot);
                ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(component, slot);
                renderContext.enterNode(component);
                if (component.construct(viewManager) instanceof SignalledObject signalledObject) {
                    signalledObject.update(viewManager, guiHolder, renderContext);
                }
                renderContext.exitNode();
            }
        }
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

}
