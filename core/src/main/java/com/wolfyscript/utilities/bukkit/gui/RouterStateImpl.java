package com.wolfyscript.utilities.bukkit.gui;

import com.google.inject.Inject;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.Router;
import com.wolfyscript.utilities.common.gui.RouterState;
import com.wolfyscript.utilities.common.gui.Window;
import com.wolfyscript.utilities.common.gui.WindowState;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class RouterStateImpl extends ComponentStateImpl<Router, RouterState> implements RouterState {

    private final Map<String, RouterState> childRouteStates = new HashMap<>();
    private final Map<String, WindowState> childComponentStates = new HashMap<>();

    @Inject
    public RouterStateImpl(@Nullable @javax.annotation.Nullable RouterState parent, Router owner) {
        super(parent, owner);
        markDirty();
    }

    @Override
    public void render(GuiHolder holder, RenderContext context) {
        //if (!shouldUpdate()) return;
        dirty = false;
        Component nextChild = ((RenderContextImpl) context).nextChild();
        ComponentState state;
        if (nextChild instanceof Window window) {
            state = Objects.requireNonNull(childComponentStates.computeIfAbsent(window.getID(), s -> window.createState(this)), () -> String.format("Failed to create child state for component '%s' of parent '%s'", window.getID(), getOwner().getID()));
        } else if (nextChild instanceof Router router) {
            state = Objects.requireNonNull(childRouteStates.computeIfAbsent(router.getID(), s -> router.createState(this)), () -> String.format("Failed to create child state for router '%s' of parent '%s'", router.getID(), getOwner().getID()));
        } else {
            throw new IllegalStateException("Unhandled Component! Cannot render component '" + nextChild.getID() + "'!");
        }
        ((RenderContextImpl) context).setCurrentNode(state);
        state.render(holder, context);
    }

    private void clearChildStates() {
        childComponentStates.clear();
    }

    void pushNewRouteState(RouterState state) {
        childRouteStates.putIfAbsent(state.getOwner().getID(), state);
    }

    void pushNewChildState(WindowState state) {
        childComponentStates.put(state.getOwner().getID(), state);
    }

    /**
     * Marks this Component as dirty and re-renders it on the next update iteration.
     */
    void markDirty() {
        super.markDirty();

    }

    Optional<RouterState> getChildRoute(String id) {
        return Optional.ofNullable(childRouteStates.get(id));
    }

    Optional<WindowState> getChildComponentState(String id) {
        return Optional.ofNullable(childComponentStates.get(id));
    }

}
