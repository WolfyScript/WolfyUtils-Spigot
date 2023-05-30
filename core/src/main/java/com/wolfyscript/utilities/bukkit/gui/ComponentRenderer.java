package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentBuilder;
import com.wolfyscript.utilities.common.gui.ComponentCollection;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.NativeRendererModule;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.Renderer;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.Signalable;
import com.wolfyscript.utilities.tuple.Pair;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ComponentRenderer<C extends ComponentState> implements Renderer<C> {

    private final ComponentStateImpl<?, ?> state;

    final Multimap<Component, Integer> componentPositions = ArrayListMultimap.create();
    final Set<ReactiveFunction<ComponentRenderer<C>>> reactiveFunctions;

    public ComponentRenderer(ComponentStateImpl<?, ?> state, Multimap<Component, Integer> componentPositions, Set<ReactiveFunction<ComponentRenderer<C>>> reactiveFunctions) {
        this.state = state;
        this.componentPositions.putAll(componentPositions);
        this.reactiveFunctions = reactiveFunctions;
    }

    @Override
    public int getWidth() {
        return state.getOwner().width();
    }

    @Override
    public int getHeight() {
        return state.getOwner().height();
    }

    @Override
    public void render(C state, GuiHolder guiHolder, RenderContext renderContext) {
        if (!(state instanceof ComponentCollection componentCollection)) return;
        for (Map.Entry<Component, Integer> entry : componentPositions.entries()) {
            if (componentCollection.get(entry.getValue()).isEmpty()) {
                renderComponent(state, entry.getValue(), entry.getKey());
            }
        }

        if (state instanceof Signalable signalable) {
            for (ReactiveFunction<ComponentRenderer<C>> reactiveFunction : reactiveFunctions) {
                if (reactiveFunction.signals().stream().anyMatch(signal -> signalable.updatedSignals().contains(signal))) {
                    Pair<Integer, Collection<ComponentBuilder<?, ?>>> result = reactiveFunction.run(this);
                    // TODO re-render
                    if (result != null) {
                        if (result.getKey() >= 0) {
                            for (ComponentBuilder<?, ?> componentBuilder : result.getValue()) {
                                renderComponent(state, result.getKey(), componentBuilder.create(null));
                            }
                        }
                    }
                }
            }
            signalable.updatedSignals().clear();
        }
        /*
        // Free up unused space/slots
        componentCollection.childComponentStates.forEach((slot, childState) -> {
            childState.getOwner().executeForAllSlots(slot, slot2 -> ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(childState, slot2));
            ((RenderContextImpl) renderContext).setSlotOffsetToParent(slot);
            ((RenderContextImpl) renderContext).setCurrentNode(childState);
            renderState(childState, guiHolder, renderContext);
        });

         */
    }

    @Override
    public Map<String, Signal<?>> getSignals() {
        return new HashMap<>();
    }

    @Override
    public void renderComponent(ComponentState state, int i, Component component) {

    }

    @Override
    public NativeRendererModule getNativeModule() {
        return null;
    }
}
