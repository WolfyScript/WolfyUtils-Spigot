package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.wolfyscript.utilities.bukkit.WolfyCoreImpl;
import com.wolfyscript.utilities.bukkit.nms.inventory.InventoryUpdate;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.NativeRendererModule;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.Renderer;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.Stateful;
import com.wolfyscript.utilities.common.gui.components.Window;
import com.wolfyscript.utilities.common.gui.components.WindowState;
import com.wolfyscript.utilities.common.items.ItemStackConfig;
import com.wolfyscript.utilities.tuple.Pair;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class WindowRenderer implements com.wolfyscript.utilities.common.gui.WindowRenderer {

    private final WindowImpl window;
    final Multimap<String, Integer> componentPositions = ArrayListMultimap.create();
    final Multimap<Signal<?>, ReactiveFunction<?>> reactiveParts;
    private final TitleFunction titleFunction;

    public WindowRenderer(WindowImpl window, TitleFunction titleFunction, Multimap<String, Integer> componentPositions, Multimap<Signal<?>, ReactiveFunction<?>> reactiveBuilders) {
        this.window = window;
        this.componentPositions.putAll(componentPositions);
        this.reactiveParts = ArrayListMultimap.create();
        this.reactiveParts.putAll(reactiveBuilders);
        this.titleFunction = titleFunction;
    }

    @Override
    public void render(WindowState state, GuiHolder guiHolder, RenderContext renderContext) {
        if (!(state instanceof WindowStateImpl windowState)) return;
        if (titleFunction instanceof StaticTitleFunction staticTitleFunction) {
            InventoryUpdate.updateInventory(((WolfyCoreImpl) window.getWolfyUtils().getCore()).getWolfyUtils().getPlugin(),
                    ((GUIHolder) guiHolder).getPlayer(), staticTitleFunction.update(null));
        } else if (titleFunction instanceof ReactiveTitleFunction<?> reactiveTitleFunction){
            for (Signal.Value<?> updatedSignal : windowState.updatedSignals()) {
                if (updatedSignal.signal().equals(reactiveTitleFunction.signal)) {
                    InventoryUpdate.updateInventory(((WolfyCoreImpl) window.getWolfyUtils().getCore()).getWolfyUtils().getPlugin(),
                            ((GUIHolder) guiHolder).getPlayer(), reactiveTitleFunction.update(updatedSignal));
                    break;
                }
            }
        }

        for (Map.Entry<String, Integer> entry : componentPositions.entries()) {
            if (windowState.get(entry.getValue()).isEmpty()) {
                renderComponent(windowState, entry.getValue(), windowState.getOwner().getChild(entry.getKey()).orElseThrow(() -> new IllegalArgumentException("Cannot find child '" + entry.getKey() + "' for component!")));
            }
        }

        for (Signal.Value<?> updatedSignal : windowState.updatedSignals()) {
            for (ReactiveFunction<?> renderFunction : reactiveParts.get(updatedSignal.signal())) {
                Pair<Integer, String> componentPosition = renderFunction.run(this, updatedSignal);
                if (componentPosition != null) {
                    if (componentPosition.getKey() >= 0) {
                        renderComponent(windowState, componentPosition.getKey(), windowState.getOwner().getChild(componentPosition.getValue()).orElseThrow(() -> new IllegalArgumentException("Cannot find child '" + componentPosition.getValue() + "' for component!")));
                    }
                }
            }
        }

        // Free up unused space/slots
        windowState.updatedSignals().clear();
        windowState.childComponentStates.forEach((slot, childState) -> {
            childState.getOwner().executeForAllSlots(slot, slot2 -> ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(childState, slot2));
            ((RenderContextImpl) renderContext).setSlotOffsetToParent(slot);
            ((RenderContextImpl) renderContext).setCurrentNode(childState);
            renderState(childState, guiHolder, renderContext);
        });
    }

    private <T extends ComponentStateImpl<B, ?>, B extends Component> void renderState(T state, GuiHolder guiHolder, RenderContext renderContext) {
        Renderer<T> renderer = (Renderer<T>) state.getOwner().getRenderer();
        renderer.render(state, guiHolder, renderContext);
    }

    @Override
    public int getWidth() {
        return window.width();
    }

    @Override
    public int getHeight() {
        return window.height();
    }

    @Override
    public void renderComponent(WindowState state, int slot, Component component) {
        if (!(state instanceof WindowStateImpl windowState)) return;
        if (state.get(slot).map(state1 -> isSameComponent(state1, component.getID())).orElse(false)) return;
        if (checkBoundsAtPos(slot, component)) {
            if (component instanceof Stateful<?> stateful) {
                state.updateComponent(slot, stateful.createState(null, windowState.viewManager));
            }
        } else {
            throw new IllegalArgumentException("Component does not fit inside of the Window!");
        }
    }

    @Override
    public NativeRendererModule getNativeModule() {
        return null;
    }

    private boolean isSameComponent(ComponentState activeState, String componentID) {
        return activeState != null && activeState.getOwner().getID().equals(componentID);
    }

    public static class ReactiveFunction<T> implements Renderer.ReactiveFunction<T, WindowRenderer> {

        private final Signal<T> signal;
        private final Function<Signal.Value<T>, Integer> slot;
        private final Function<Signal.Value<T>, String> selector;

        public ReactiveFunction(Signal<T> signal, Function<Signal.Value<T>, Integer> slot, Function<Signal.Value<T>, String> selector) {
            this.signal = signal;
            this.slot = slot;
            this.selector = selector;
        }

        @Override
        public Pair<Integer, String> run(WindowRenderer renderer, Signal.Value<?> value) {
            if (!signal.equals(value.signal())) return null;
            @SuppressWarnings("unchecked")
            Signal.Value<T> typedValue = (Signal.Value<T>) value;
            return new Pair<>(slot.apply(typedValue), selector.apply(typedValue));
        }

        @Override
        public Signal<T> getSignal() {
            return signal;
        }
    }

    public static class ReactiveTitleFunction<T_SIGNAL_VALUE> implements TitleFunction {

        private final Signal<T_SIGNAL_VALUE> signal;
        private final Function<Signal.Value<T_SIGNAL_VALUE>, net.kyori.adventure.text.Component> updateFunction;

        public ReactiveTitleFunction(Signal<T_SIGNAL_VALUE> signal, Function<Signal.Value<T_SIGNAL_VALUE>, net.kyori.adventure.text.Component> updateFunction) {
            this.signal = signal;
            this.updateFunction = updateFunction;
        }

        public net.kyori.adventure.text.Component update(Signal.Value<?> value) {
            if (!signal.equals(value.signal())) return net.kyori.adventure.text.Component.empty();
            return updateFunction.apply((Signal.Value<T_SIGNAL_VALUE>) value);
        }

    }

    public static class StaticTitleFunction implements TitleFunction {

        private final net.kyori.adventure.text.Component textComponent;

        public StaticTitleFunction(net.kyori.adventure.text.Component textComponent) {
            this.textComponent = textComponent;
        }

        @Override
        public net.kyori.adventure.text.Component update(Signal.Value<?> value) {
            return textComponent;
        }
    }

    public interface TitleFunction {

        net.kyori.adventure.text.Component update(Signal.Value<?> value);

    }


    public static class Builder implements com.wolfyscript.utilities.common.gui.WindowRenderer.Builder {

        final Multimap<String, Integer> componentPositions = ArrayListMultimap.create();
        final Set<String> staticParts = new HashSet<>();
        final Multimap<Signal<?>, ReactiveFunction<?>> reactiveParts = ArrayListMultimap.create();
        final Map<Integer, BukkitItemStackConfig> stackRenderList = new HashMap<>();
        private TitleFunction titleFunction;

        public Builder(Multimap<String, Integer> componentPositions) {
            if (componentPositions == null) return;
            this.componentPositions.putAll(componentPositions);
        }

        @Override
        public <T> Signal<T> useSignal(String s, Class<T> aClass, Function<ComponentState, T> defaultValueFunction) {
            return new SignalImpl<>(s, aClass, defaultValueFunction);
        }

        @Override
        public Builder position(int i, String s) {
            this.componentPositions.put(s, i);
            return this;
        }

        @Override
        public Builder renderAt(int i, ItemStackConfig<?> itemStackConfig) {
            if (itemStackConfig instanceof BukkitItemStackConfig bukkitItemStackConfig) {
                this.stackRenderList.put(i, bukkitItemStackConfig);
            }
            return this;
        }

        @Override
        public <S> Builder position(Signal<S> signal, Function<Signal.Value<S>, Integer> slot, Function<Signal.Value<S>, String> selector) {
            this.reactiveParts.put(signal, new ReactiveFunction<>(signal, slot, selector));
            return this;
        }

        @Override
        public <S> Builder render(Signal<S> signal, Function<Signal.Value<S>, String> selector) {
            this.reactiveParts.put(signal, new ReactiveFunction<>(signal, sValue -> -1, selector));
            return this;
        }

        @Override
        public <S> Builder renderAt(Signal<S> signal, Function<Signal.Value<S>, Integer> slot, Function<Signal.Value<S>, String> selector) {
            this.reactiveParts.put(signal, new ReactiveFunction<>(signal, slot, selector));
            return this;
        }

        @Override
        public Builder renderAt(int i, String s) {
            this.componentPositions.put(s, i);
            this.staticParts.add(s);
            return this;
        }

        @Override
        public Builder render(String componentId) {
            staticParts.add(componentId);
            return this;
        }

        public WindowRenderer create(Window window) {
            Multimap<String, Integer> finalPostions = ArrayListMultimap.create();
            for (String staticPart : staticParts) {
                Collection<Integer> slots = this.componentPositions.get(staticPart);
                for (Integer slot : slots) {
                    finalPostions.put(staticPart, slot);
                }
            }
            return new WindowRenderer((WindowImpl) window, titleFunction, finalPostions, reactiveParts);
        }

        @Override
        public Builder title(net.kyori.adventure.text.Component component) {
            this.titleFunction = new StaticTitleFunction(component);
            return this;
        }

        @Override
        public <S> Builder title(Signal<S> signal, Function<Signal.Value<S>, net.kyori.adventure.text.Component> function) {
            this.titleFunction = new ReactiveTitleFunction<>(signal, function);
            return this;
        }
    }


}
