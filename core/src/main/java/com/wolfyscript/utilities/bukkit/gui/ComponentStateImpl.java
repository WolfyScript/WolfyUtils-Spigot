package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.Interactable;
import com.wolfyscript.utilities.common.gui.InteractionDetails;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.Signalable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class ComponentStateImpl<OWNER extends Component, PARENT extends ComponentState> implements ComponentState {

    private final PARENT parent;
    private final OWNER owner;
    protected final Map<String, Signal<?>> messageValues = new HashMap<>();
    boolean dirty = false;

    public ComponentStateImpl(PARENT parent, OWNER owner) {
        this.parent = parent;
        this.owner = owner;
        markDirty();
    }

    void updateMessage(Signal<?> message) {
        messageValues.put(message.key(), message);
        markDirty();
    }

    @Override
    public <T> Signal<T> captureSignal(String signalKey, Class<T> msgType) {
        Signal<?> message = messageValues.get(signalKey);
        if (message == null)
            return getParent() != null ? getParent().captureSignal(signalKey, msgType) : null;
        Preconditions.checkState(Objects.equals(message.key(), signalKey) && message.valueType() == msgType, "Failed to capture Signal! Invalid key or type! Expected %s, but got %s", message.valueType(), msgType);
        return (Signal<T>) message;
    }

    public Signal<?> captureSignal(String signalKey) {
        Signal<?> value = messageValues.get(signalKey);
        if (value == null) {
            return getParent() != null ? getParent().captureSignal(signalKey) : null;
        }
        return value;
    }

    public InteractionResult interact(GuiHolder holder, InteractionDetails interactionDetails) {
        if (parent != null) {
            InteractionResult result = parent.interact(holder, interactionDetails);
            if (result.isCancelled()) return result;
            parent.getOwner().getRenderer().getSignals().values().forEach(signal -> signal.enter(holder.getViewManager()));
        }
        if (owner instanceof Interactable interactable) {
            owner.getRenderer().getSignals().values().forEach(signal -> signal.enter(holder.getViewManager()));
            InteractionResult result = interactable.interactCallback().interact(holder, this, interactionDetails);
            owner.getRenderer().getSignals().values().forEach(Signal::exit);
            if (parent != null) {
                parent.getOwner().getRenderer().getSignals().values().forEach(signal -> {
                    if (signal.exit() && parent instanceof Signalable signalable) {
                        signalable.receiveUpdate(signal);
                    }
                });
            }
            return result;
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
