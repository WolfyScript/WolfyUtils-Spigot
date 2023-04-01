package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.Interactable;
import com.wolfyscript.utilities.common.gui.InteractionDetails;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.StateHook;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class ComponentStateImpl<OWNER extends Component, PARENT extends ComponentState> implements ComponentState {

    private final PARENT parent;
    private final OWNER owner;
    private final Map<String, StateHook<?>> hooks = new HashMap<>();
    private boolean dirty = false;

    public ComponentStateImpl(PARENT parent, OWNER owner) {
        this.parent = parent;
        this.owner = owner;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> StateHook<V> getOrCreateHook(String id, Supplier<V> defaultValue) {
        return (StateHook<V>) hooks.computeIfAbsent(id, s -> new StateHookImpl<>(this, defaultValue.get()));
    }

    public abstract void render(GuiHolder holder, RenderContext context);

    public InteractionResult interact(GuiHolder holder, InteractionDetails interactionDetails) {
        if (parent != null) {
            InteractionResult result = parent.interact(holder, interactionDetails);
            if (result.isCancelled()) return result;
        }
        if (owner instanceof Interactable interactable) {
            return interactable.interactCallback().interact(holder, this, interactionDetails);
        }
        return InteractionResult.def();
    }

    public PARENT getParent() {
        return parent;
    }

    public OWNER getOwner() {
        return owner;
    }

    @Override
    public boolean shouldUpdate() {
        return dirty;
    }

    /**
     * Marks this Component as dirty and re-renders it on the next update iteration.
     */
    void markDirty() {
        this.dirty = true;
    }

}
