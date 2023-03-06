package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.Router;
import com.wolfyscript.utilities.common.gui.Window;
import java.util.ArrayList;
import java.util.List;

public class ComponentStateRouterImpl extends ComponentStateImpl<Router, ComponentStateRouterImpl> {

    private final List<ComponentStateRouterImpl> childRouteStates = new ArrayList<>();
    private final List<ComponentStateImpl<? extends Window, ComponentStateRouterImpl>> childComponentStates = new ArrayList<>();

    public ComponentStateRouterImpl(ComponentStateRouterImpl parent, Router owner) {
        super(parent, owner);
    }

    @Override
    public void render(GuiHolder holder, RenderContext context) {
        if (!shouldUpdate()) return;
        for (ComponentStateImpl<? extends Window, ComponentStateRouterImpl> childComponentState : childComponentStates) {
            childComponentState.render(holder, context);
        }
        for (ComponentStateRouterImpl childRouteState : childRouteStates) {
            childRouteState.render(holder, context);
        }
    }

    private void clearChildStates() {
        childComponentStates.clear();
    }

    void pushNewChildState(ComponentStateImpl<? extends Window, ComponentStateRouterImpl> state) {
        childComponentStates.add(state);
    }

    /**
     * Marks this Component as dirty and re-renders it on the next update iteration.
     */
    void markDirty() {
        super.markDirty();

    }

}
