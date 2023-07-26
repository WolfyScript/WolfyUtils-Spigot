package com.wolfyscript.utilities.bukkit.gui.components;

import com.google.common.collect.Multimap;
import com.wolfyscript.utilities.bukkit.gui.GuiViewManagerImpl;
import com.wolfyscript.utilities.bukkit.gui.RenderContextImpl;
import com.wolfyscript.utilities.common.gui.*;
import com.wolfyscript.utilities.common.gui.components.ComponentCluster;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;

import java.util.Map;

public class ComponentClusterRendererImpl implements Renderer {

    private final ComponentClusterImpl componentCluster;
    private final Multimap<Component, Integer> children;
    private final GuiViewManager viewManager;

    public ComponentClusterRendererImpl(ComponentClusterImpl componentCluster, GuiViewManager viewManager, Multimap<Component, Integer> children) {
        this.componentCluster = componentCluster;
        this.viewManager = viewManager;
        this.children = children;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void render(GuiHolder guiHolder, RenderContext context) {
        if (!(context instanceof RenderContextImpl renderContext)) return;

        for (Map.Entry<Component, Integer> entry : children.entries()) {
            int slot = entry.getValue();
            Component component = entry.getKey();
            entry.getKey().executeForAllSlots(slot, slot2 -> ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(component, slot2));
            renderContext.setSlotOffsetToParent(slot);
            renderContext.enterNode(component);
            if (component.construct(viewManager) instanceof SignalledObject signalledObject) {
                signalledObject.update(viewManager, guiHolder, renderContext);
            }
            renderContext.exitNode();
        }
    }

    @Override
    public Map<String, Signal<?>> getSignals() {
        return Object2ObjectMaps.emptyMap();
    }

    @Override
    public NativeRendererModule<?> getNativeModule() {
        return null;
    }
}
