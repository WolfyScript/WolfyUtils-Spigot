package com.wolfyscript.utilities.bukkit.gui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.WindowState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.SizedComponent;
import com.wolfyscript.utilities.common.gui.Window;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;

public class WindowStateImpl extends ComponentStateImpl<Window, ComponentStateRouterImpl> implements WindowState {

    private final Map<Integer, ComponentStateImpl<? extends SizedComponent, WindowStateImpl>> childComponentStates = new Int2ObjectOpenHashMap<>();

    public WindowStateImpl(ComponentStateRouterImpl parent, WindowImpl owner) {
        super(parent, owner);
    }

    @Override
    public void render(GuiHolder holder, RenderContext context) {
        getOwner().renderCallback().render(holder, this);
    }

    void pushNewChildState(int slot, ComponentStateImpl<? extends SizedComponent, WindowStateImpl> state) {
        childComponentStates.put(slot, state);
    }

    @Override
    public void setComponent(int slot, String componentID) {
        Component component = getOwner().getChild(componentID).orElseThrow(() -> new IllegalArgumentException("Cannot find child '" + componentID + "' for component!"));
        //TODO: Create and add child state to current state
        Injector injector = Guice.createInjector(binder -> {
            binder.bindConstant().annotatedWith(Names.named("position")).to(slot);
            binder.bind(WolfyUtils.class).toInstance(component.getWolfyUtils());
            binder.bind(WindowState.class).toInstance(this);
        });
        if (checkBoundsAtPos(slot, component)) {
            ComponentState state = injector.getInstance(component.getComponentStateType());
            if (state instanceof ComponentStateImpl<?, ?> impl && impl.getParent() instanceof WindowState) {
                pushNewChildState(slot, (ComponentStateImpl<? extends SizedComponent, WindowStateImpl>) state);
            }
        } else {
            throw new IllegalArgumentException("Component does not fit inside of the Window!");
        }
    }

    private boolean checkBoundsAtPos(int i, Component component) throws IllegalStateException {
        if (component instanceof SizedComponent sizedComponent) {
            int parentWidth = sizedComponent.width();
            int parentHeight = sizedComponent.height();
            return i > 0 && i < parentWidth * parentHeight && (i / parentHeight) + sizedComponent.width() < parentWidth && (i / parentWidth) + sizedComponent.height() < parentHeight;
        }
        return false;
    }

}
