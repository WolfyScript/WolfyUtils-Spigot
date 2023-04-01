package com.wolfyscript.utilities.bukkit.gui;

import com.google.inject.Inject;
import com.wolfyscript.utilities.common.gui.Component;
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

    @Inject
    public WindowStateImpl(RouterState parent, Window owner) {
        super(parent, owner);
        markDirty();
    }

    @Override
    public void render(GuiHolder holder, RenderContext context) {
        if (!shouldUpdate()) return;
        dirty = false;
        getOwner().renderCallback().render(holder, this);
        childComponentStates.forEach((integer, childState) -> {
            Component owner = childState.getOwner();
            if (owner instanceof SizedComponent sized) {
                int width = sized.width();
                int height = sized.height();
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        int slot = integer + j + i * (9 - width);
                        ((GuiViewManagerImpl) holder.getViewManager()).updateTailNodes(childState, slot);
                    }
                }
            }
            ((RenderContextImpl) context).setSlotOffsetToParent(integer);
            ((RenderContextImpl) context).setCurrentNode(childState);
            childState.render(holder, context);
        });
    }

    void pushNewChildState(int slot, ComponentStateImpl<? extends SizedComponent, WindowStateImpl> state) {
        childComponentStates.put(slot, state);
    }

    @Override
    public void setComponent(int slot, String componentID) {
        Component component = getOwner().getChild(componentID).orElseThrow(() -> new IllegalArgumentException("Cannot find child '" + componentID + "' for component!"));
        if (checkBoundsAtPos(slot, component)) {
            if (component instanceof Stateful<?> stateful) {
                pushNewChildState(slot, (ComponentStateImpl<? extends SizedComponent, WindowStateImpl>) stateful.createState(this));
            }
        } else {
            throw new IllegalArgumentException("Component does not fit inside of the Window!");
        }
    }

    private boolean checkBoundsAtPos(int i, Component component) throws IllegalStateException {
        if (component instanceof SizedComponent sizedComponent) {
            int parentWidth = getOwner().width();
            int parentHeight = getOwner().height();
            return i > 0 && i < parentWidth * parentHeight && (i / parentHeight) + sizedComponent.width() < parentWidth && (i / parentWidth) + sizedComponent.height() < parentHeight;
        }
        return false;
    }

}
