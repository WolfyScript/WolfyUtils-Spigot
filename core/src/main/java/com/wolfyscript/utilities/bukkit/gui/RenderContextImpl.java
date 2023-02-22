package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.RenderContext;
import java.util.Optional;
import org.bukkit.inventory.Inventory;

public class RenderContextImpl implements RenderContext {

    private Inventory inventory;
    private ComponentStateImpl currentNode;

    public RenderContextImpl(Inventory inventory) {
        this.inventory = inventory;
        this.currentNode = null;
    }

    public Optional<ComponentStateImpl> getCurrentNode() {
        return Optional.ofNullable(currentNode);
    }

    @Override
    public void setChildComponent(int i, String s) {
        Component component = null; // TODO: get Component
        if (component instanceof ComponentImpl componentImpl) {
            currentNode.pushNewChildState(componentImpl.createNewState(currentNode, i));
        }
    }

    @Override
    public ComponentState getCurrentState() {
        return currentNode;
    }

}
