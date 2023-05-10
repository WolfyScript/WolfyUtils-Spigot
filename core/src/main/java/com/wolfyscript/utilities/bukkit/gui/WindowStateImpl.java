package com.wolfyscript.utilities.bukkit.gui;

import com.google.inject.Inject;
import com.wolfyscript.utilities.bukkit.WolfyCoreImpl;
import com.wolfyscript.utilities.bukkit.nms.inventory.InventoryUpdate;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.RouterState;
import com.wolfyscript.utilities.common.gui.SizedComponent;
import com.wolfyscript.utilities.common.gui.Stateful;
import com.wolfyscript.utilities.common.gui.Window;
import com.wolfyscript.utilities.common.gui.WindowState;
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
            if (holder instanceof GUIHolder guiHolder) {
                InventoryUpdate.updateInventory(((WolfyCoreImpl) getOwner().getWolfyUtils().getCore()).getWolfyUtils().getPlugin(),
                        guiHolder.getPlayer(),
                        getOwner().createTitle(holder));
            }
            getOwner().getRenderOptions().renderCallback().ifPresentOrElse(
                    renderCallback -> renderCallback.render(holder, this),
                    () -> getOwner().getRenderOptions().placement().forEach(this::renderComponent)
            );

            // Free up unused space/slots
            for (var entry : childComponentStates.entrySet()) {
                var updatedState = updatedStateCache.get(entry.getKey());
                if (updatedState != null && updatedState.getOwner().getID().equals(entry.getValue().getOwner().getID()))
                    continue;
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
    public void renderComponent(int slot, String componentID) {
        renderComponent(slot, getOwner().getChild(componentID).orElseThrow(() -> new IllegalArgumentException("Cannot find child '" + componentID + "' for component!")));
    }

    @Override
    public void renderComponent(String componentID) {
        Component component = getOwner().getChild(componentID).orElseThrow(() -> new IllegalArgumentException("Cannot find child '" + componentID + "' for component!"));
        for (int slot : getOwner().getRenderOptions().getSlotsFor(component)) {
            renderComponent(slot, component);
        }
    }

    private void renderComponent(int slot, Component component) {
        updatedStateCache.compute(slot, (slotKey, currentState) -> {
            // Keep existing states intact so that they are not reset to their initial value
            ComponentStateImpl<? extends SizedComponent, WindowStateImpl> activeState = childComponentStates.get(slot);
            if (isSameComponent(activeState, component.getID())) return activeState;
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

    private boolean isSameComponent(ComponentState activeState, String componentID) {
        return activeState != null && activeState.getOwner().getID().equals(componentID);
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
