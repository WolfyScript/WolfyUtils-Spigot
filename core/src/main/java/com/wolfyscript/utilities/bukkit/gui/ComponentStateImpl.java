package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.Interactable;
import com.wolfyscript.utilities.common.gui.InteractionDetails;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.Signal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class ComponentStateImpl<OWNER extends Component, PARENT extends ComponentState> implements ComponentState {

    private final PARENT parent;
    private final OWNER owner;
    protected final Map<String, Signal.Value<?>> messageValues = new HashMap<>();
    boolean dirty = false;

    public ComponentStateImpl(PARENT parent, OWNER owner) {
        this.parent = parent;
        this.owner = owner;
        markDirty();
    }

    void updateMessage(Signal.Value<?> message) {
        messageValues.put(message.signal().key(), message);
        markDirty();
    }

    @Override
    public <T> Signal.Value<T> captureSignal(String signalKey, Class<T> msgType) {
        Signal.Value<?> message = messageValues.get(signalKey);
        if (message == null)
            return getParent() != null ? getParent().captureSignal(signalKey, msgType) : null;
        Preconditions.checkState(Objects.equals(message.signal().key(), signalKey) && message.signal().valueType() == msgType, "Failed to capture Signal! Invalid key or type! Expected %s, but got %s", message.signal().valueType(), msgType);
        return (Signal.Value<T>) message;
    }

    public Signal.Value<?> captureSignal(String signalKey) {
        Signal.Value<?> value = messageValues.get(signalKey);
        if (value == null) {
            return getParent() != null ? getParent().captureSignal(signalKey) : null;
        }
        return value;
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
        if (dirty) {
            dirty = false;
            return true;
        }
        return false;
    }

    /**
     * Marks this Component as dirty and re-renders it on the next update iteration.
     */
    void markDirty() {
        this.dirty = true;
    }

}
