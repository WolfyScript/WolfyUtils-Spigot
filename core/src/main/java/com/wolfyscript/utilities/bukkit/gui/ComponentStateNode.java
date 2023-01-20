package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.Data;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ComponentStateNode<D extends Data> {

    private final ComponentStateNode<D> parent;
    private final Component<D> owner;
    private int stateIndex;
    private ComponentState<D> state;

    public ComponentStateNode(ComponentStateNode<D> parent, Component<D> owner, int stateIndex, ComponentState<D> state) {
        this.parent = parent;
        this.owner = owner;
        this.stateIndex = stateIndex;
        this.state = state;
    }

    public ComponentStateNode<D> getParent() {
        return parent;
    }

    public Component<D> getOwner() {
        return owner;
    }

    public int getStateIndex() {
        return stateIndex;
    }

    public ComponentState<D> getState() {
        return state;
    }

    public static class WindowComponentStateNode<D extends Data> extends ComponentStateNode<D> {

        private final Map<Integer, ComponentStateNode<D>> childrenPerSlot = new HashMap<>();

        public WindowComponentStateNode(ComponentStateNode<D> parent, Component<D> owner, int stateIndex, ComponentState<D> state) {
            super(parent, owner, stateIndex, state);
        }

        void setChild(int slot, ComponentStateNode<D> child) {
            childrenPerSlot.put(slot, child);
        }

        Optional<ComponentStateNode<D>> getChild(int slot) {
            return Optional.of(childrenPerSlot.get(slot));
        }

    }

}
