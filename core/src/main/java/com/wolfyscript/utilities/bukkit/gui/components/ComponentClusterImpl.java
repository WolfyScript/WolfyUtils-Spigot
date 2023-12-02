package com.wolfyscript.utilities.bukkit.gui.components;

import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.gui.GuiViewManagerImpl;
import com.wolfyscript.utilities.bukkit.gui.RenderContextImpl;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.*;
import com.wolfyscript.utilities.common.gui.components.ComponentCluster;
import com.wolfyscript.utilities.common.gui.impl.AbstractComponentImpl;

import java.util.*;

@KeyedStaticId(key = "cluster")
public class ComponentClusterImpl extends AbstractComponentImpl implements ComponentCluster {

    private int width;
    private int height;
    private final List<Component> children;

    public ComponentClusterImpl(String internalID, WolfyUtils wolfyUtils, Component parent, Position position, List<Component> children) {
        super(internalID, wolfyUtils, parent, position);
        this.children = children;

        int topLeft = 54;
        int bottomRight = 0;

        for (Component child : this.children) {
            if (child.position().type() == Position.Type.RELATIVE) {
                // Only take relative positions into account
                topLeft = Math.min(child.position().slot(), topLeft);
                bottomRight = Math.max(child.position().slot(), bottomRight);
            }
        }
        this.width = Math.abs((topLeft % 9) - (bottomRight % 9)) + 1;
        this.height = Math.abs((topLeft / 9) - (bottomRight / 9)) + 1;
    }

    public ComponentClusterImpl(ComponentClusterImpl staticComponent) {
        super(staticComponent.getID(), staticComponent.getWolfyUtils(), staticComponent.parent(), staticComponent.position());
        this.children = staticComponent.children;
    }

    @Override
    public Set<? extends Component> childComponents() {
        return new HashSet<>(children);
    }

    @Override
    public Optional<? extends Component> getChild(String id) {
        return Optional.empty();
    }

    @Override
    public ComponentCluster construct(GuiHolder holder, GuiViewManager guiViewManager) {
        return this;
    }

    @Override
    public void remove(GuiHolder guiHolder, GuiViewManager guiViewManager, RenderContext renderContext) {
        for (Component component : children) {
            component.remove(guiHolder, guiViewManager, renderContext);
        }
    }

    @Override
    public void update(GuiViewManager viewManager, GuiHolder guiHolder, RenderContext context) {
        if (!(context instanceof RenderContextImpl renderContext)) return;

        for (Component component : children) {
            var childPos = component.position();
            ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(component, childPos.slot());
            renderContext.enterNode(component);
            if (component.construct(guiHolder, viewManager) instanceof SignalledObject signalledObject) {
                signalledObject.update(viewManager, guiHolder, renderContext);
            }
            renderContext.exitNode();
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
