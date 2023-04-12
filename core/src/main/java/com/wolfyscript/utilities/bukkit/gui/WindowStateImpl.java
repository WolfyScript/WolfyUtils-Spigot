package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.wolfyscript.utilities.bukkit.world.inventory.ItemUtils;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.RouterState;
import com.wolfyscript.utilities.common.gui.Stateful;
import com.wolfyscript.utilities.common.gui.WindowState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.SizedComponent;
import com.wolfyscript.utilities.common.gui.Window;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;

public class WindowStateImpl extends ComponentStateImpl<Window, RouterState> implements WindowState {

    private final Map<Integer, ComponentStateImpl<? extends SizedComponent, WindowStateImpl>> childComponentStates = new Int2ObjectOpenHashMap<>();
    private final Map<Integer, ComponentStateImpl<? extends SizedComponent, WindowStateImpl>> updatedStateCache = new Int2ObjectOpenHashMap<>();

    @Inject
    public WindowStateImpl(RouterState parent, Window owner) {
        super(parent, owner);
        markDirty();
    }

    @Override
    public void render(GuiHolder holder, RenderContext context) {
        if (shouldUpdate()) {
            updatedStateCache.clear();
            getOwner().renderCallback().render(holder, this);
            // Free up unused space/slots
            for (var entry : childComponentStates.entrySet()) {
                var updatedState = updatedStateCache.get(entry.getKey());
                if (updatedState != null && updatedState.getOwner().getID().equals(entry.getValue().getOwner().getID())) continue;
                entry.getValue().getOwner().executeForAllSlots(entry.getKey(), slot -> {
                    ((GuiViewManagerImpl) holder.getViewManager()).updateTailNodes(null, slot);
                    context.setStack(slot, null);
                });
            }
            childComponentStates.clear();
            childComponentStates.putAll(updatedStateCache);
        }
        childComponentStates.forEach((slot, childState) -> {
            childState.getOwner().executeForAllSlots(slot, slot2 -> ((GuiViewManagerImpl) holder.getViewManager()).updateTailNodes(childState, slot2));
            ((RenderContextImpl) context).setSlotOffsetToParent(slot);
            ((RenderContextImpl) context).setCurrentNode(childState);
            childState.render(holder, context);
        });
    }

    @Override
    public void setComponent(int slot, String componentID) {
        updatedStateCache.compute(slot, (slotKey, currentState) -> {
            // Keep existing states intact so that they are not reset to their initial value
            ComponentStateImpl<? extends SizedComponent, WindowStateImpl> activeState = childComponentStates.get(slot);
            if (activeState != null) {
                if (activeState.getOwner().getID().equals(componentID)) {
                    return activeState;
                }
            }
            Component component = getOwner().getChild(componentID).orElseThrow(() -> new IllegalArgumentException("Cannot find child '" + componentID + "' for component!"));
            if (checkBoundsAtPos(slot, component)) {
                if (component instanceof Stateful<?> stateful) {
                    return (ComponentStateImpl<? extends SizedComponent, WindowStateImpl>) stateful.createState(this);
                }
                // TODO: Non-Stateful components?
            } else {
                throw new IllegalArgumentException("Component does not fit inside of the Window!");
            }
            return null;
        });
    }

    private boolean checkBoundsAtPos(int i, Component component) throws IllegalStateException {
        if (component instanceof SizedComponent sizedComponent) {
            int parentWidth = getOwner().width();
            int parentHeight = getOwner().height();
            return i > 0 && i < parentWidth * parentHeight && (i / parentHeight) + sizedComponent.width() <= parentWidth && (i / parentWidth) + sizedComponent.height() <= parentHeight;
        }
        return false;
    }

}
