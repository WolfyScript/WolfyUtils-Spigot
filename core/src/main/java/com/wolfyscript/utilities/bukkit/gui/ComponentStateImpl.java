package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.StateHook;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ComponentStateImpl implements ComponentState {

    private final ComponentStateImpl parent;
    private final Component owner;
    private final List<ComponentStateImpl> childStates = new ArrayList<>();
    private final Map<String, StateHook<?>> hooks = new HashMap<>();
    private boolean dirty = false;
    private final Deque<ComponentStateImpl> childrenToUpdate = new ArrayDeque<>();
    private final int relativePos;

    public ComponentStateImpl(ComponentStateImpl parent, Component owner) {
        this.parent = parent;
        this.owner = owner;
        this.relativePos = -1;
    }

    public ComponentStateImpl(ComponentStateImpl parent, Component owner, int relativePos) {
        this.parent = parent;
        this.owner = owner;
        this.relativePos = relativePos;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> StateHook<V> getOrCreateHook(String id, Supplier<V> defaultValue) {
        return (StateHook<V>) hooks.computeIfAbsent(id, s -> new StateHookImpl<>(this, defaultValue.get()));
    }

    public void render(GuiHolder holder, RenderContext context) {
        if (dirty) {
            this.clearChildStates();
            owner.render(holder, this, context);
        }

        if (childStates.isEmpty()) {
            // Construct new states, as there are none to update. (Component changed)
            for (Component child : owner.children()) {
                ComponentStateImpl childState = new ComponentStateImpl(this, child);
                childStates.add(childState);
                childrenToUpdate.push(childState);
            }
        }

        ComponentStateImpl current;
        while ((current = childrenToUpdate.poll()) != null) {
            current.render(holder, context);
        }
    }

    private void clearChildStates() {
        childStates.clear();
    }

    void pushNewChildState(ComponentStateImpl state) {
        childStates.add(state);
    }

    int getRelativePos() {
        return relativePos;
    }

    public ComponentStateImpl getParent() {
        return parent;
    }

    public Component getOwner() {
        return owner;
    }

    @Override
    public boolean shouldUpdate() {
        return dirty || !childrenToUpdate.isEmpty();
    }

    /**
     * Marks the child of this Component as dirty.
     *
     * @param childCause The child that was marked dirty and requires an update.
     */
    void markChildDirty(ComponentStateImpl childCause) {
        childrenToUpdate.push(childCause);
    }

    /**
     * Marks this Component as dirty and re-renders it on the next update iteration.
     */
    void markDirty() {
        this.dirty = true;
        if (parent != null) {
            parent.markChildDirty(this);
        }
    }

}
