package com.wolfyscript.utilities.bukkit.gui;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreImpl;
import com.wolfyscript.utilities.bukkit.nms.inventory.InventoryUpdate;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentBuilder;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.NativeRendererModule;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.Renderer;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.Stateful;
import com.wolfyscript.utilities.common.gui.Window;
import com.wolfyscript.utilities.common.gui.WindowBuilder;
import com.wolfyscript.utilities.common.gui.WindowState;
import com.wolfyscript.utilities.common.gui.util.SerializableSupplier;
import com.wolfyscript.utilities.common.items.ItemStackConfig;
import com.wolfyscript.utilities.common.registry.RegistryGUIComponentBuilders;
import com.wolfyscript.utilities.tuple.Pair;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class WindowRenderer implements com.wolfyscript.utilities.common.gui.WindowRenderer {

    private final WindowImpl window;
    final Multimap<Component, Integer> componentPositions = ArrayListMultimap.create();
    final Set<ReactiveFunction> reactiveFunctions;
    private final TitleFunction titleFunction;
    private final Map<String, Signal<?>> signals = new HashMap<>();

    public WindowRenderer(WindowImpl window, TitleFunction titleFunction, Multimap<Component, Integer> componentPositions, Set<ReactiveFunction> reactiveFunctions) {
        this.window = window;
        this.componentPositions.putAll(componentPositions);
        this.reactiveFunctions = reactiveFunctions;
        this.titleFunction = titleFunction;
    }

    @Override
    public void render(WindowState state, GuiHolder guiHolder, RenderContext renderContext) {
        if (!(state instanceof WindowStateImpl windowState)) return;
        if (titleFunction instanceof StaticTitleFunction staticTitleFunction) {
            InventoryUpdate.updateInventory(((WolfyCoreImpl) window.getWolfyUtils().getCore()).getWolfyUtils().getPlugin(),
                    ((GUIHolder) guiHolder).getPlayer(), staticTitleFunction.update(null));
        } else if (titleFunction instanceof ReactiveTitleFunction<?> reactiveTitleFunction) {
            for (Signal<?> updatedSignal : windowState.updatedSignals()) {
                if (updatedSignal.equals(reactiveTitleFunction.signal)) {
                    InventoryUpdate.updateInventory(((WolfyCoreImpl) window.getWolfyUtils().getCore()).getWolfyUtils().getPlugin(),
                            ((GUIHolder) guiHolder).getPlayer(), reactiveTitleFunction.update(updatedSignal));
                    break;
                }
            }
        }

        for (Map.Entry<Component, Integer> entry : componentPositions.entries()) {
            if (windowState.get(entry.getValue()).isEmpty()) {
                renderComponent(windowState, entry.getValue(), entry.getKey());
            }
        }

        for (ReactiveFunction reactiveFunction : reactiveFunctions) {
            if (reactiveFunction.signals().stream().anyMatch(signal -> windowState.updatedSignals().contains(signal))) {
                Pair<Integer, Collection<ComponentBuilder<?,?>>> result = reactiveFunction.run(this);
                // TODO re-render
                if (result != null) {
                    if (result.getKey() >= 0) {
                        for (ComponentBuilder<?, ?> componentBuilder : result.getValue()) {
                            renderComponent(windowState, result.getKey(), componentBuilder.create(null));
                        }
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
    public Map<String, Signal<?>> getSignals() {
        return signals;
    }

    @Override
    public NativeRendererModule getNativeModule() {
        return null;
    }

    private boolean isSameComponent(ComponentState activeState, String componentID) {
        return activeState != null && activeState.getOwner().getID().equals(componentID);
    }

    public static class ReactiveFunction implements Renderer.ReactiveFunction<WindowRenderer> {

        private static int NEXT_ID = 0;

        private final int id;
        private final List<Signal<?>> signals;
        private final Supplier<Collection<ComponentBuilder<?,?>>> function;

        public ReactiveFunction(List<Signal<?>> signals, Supplier<Collection<ComponentBuilder<?,?>>> function) {
            this.id = NEXT_ID++;
            this.signals = signals;
            this.function = function;
        }

        @Override
        public int id() {
            return id;
        }

        @Override
        public Pair<Integer, Collection<ComponentBuilder<?,?>>> run(WindowRenderer renderer) {
            return new Pair<>(0, function.get());
        }

        @Override
        public List<Signal<?>> signals() {
            return signals;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ReactiveFunction that = (ReactiveFunction) o;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    public static class ReactiveTitleFunction<T_SIGNAL_VALUE> implements TitleFunction {

        private final Signal<T_SIGNAL_VALUE> signal;
        private final Function<Signal<T_SIGNAL_VALUE>, net.kyori.adventure.text.Component> updateFunction;

        public ReactiveTitleFunction(Signal<T_SIGNAL_VALUE> signal, Function<Signal<T_SIGNAL_VALUE>, net.kyori.adventure.text.Component> updateFunction) {
            this.signal = signal;
            this.updateFunction = updateFunction;
        }

        public net.kyori.adventure.text.Component update(Signal<?> value) {
            if (!signal.equals(value)) return net.kyori.adventure.text.Component.empty();
            return updateFunction.apply((Signal<T_SIGNAL_VALUE>) value);
        }

    }

    public static class StaticTitleFunction implements TitleFunction {

        private final net.kyori.adventure.text.Component textComponent;

        public StaticTitleFunction(net.kyori.adventure.text.Component textComponent) {
            this.textComponent = textComponent;
        }

        @Override
        public net.kyori.adventure.text.Component update(Signal<?> value) {
            return textComponent;
        }
    }

    public interface TitleFunction {

        net.kyori.adventure.text.Component update(Signal<?> value);

    }

    public static class Builder implements com.wolfyscript.utilities.common.gui.WindowRenderer.Builder {

        final WolfyUtils wolfyUtils;
        final Multimap<ComponentBuilder<?, ?>, Integer> componentBuilderPositions = ArrayListMultimap.create();
        final Set<ComponentBuilder<?, ?>> componentRenderSet = new HashSet<>();
        final Set<ReactiveFunction> reactiveFunctions = new HashSet<>();
        final Map<Integer, BukkitItemStackConfig> stackRenderList = new HashMap<>();
        final Map<String, Signal<?>> usedSignals = new HashMap<>();
        private TitleFunction titleFunction;
        private WindowBuilder parentBuilder;

        @JsonCreator
        public Builder(@JacksonInject WolfyUtils wolfyUtils) {
            this.wolfyUtils = wolfyUtils;
        }

        @JsonIgnore
        protected void setParentBuilder(WindowBuilder parentBuilder) {
            this.parentBuilder = parentBuilder;
        }

        @JsonSetter("placement")
        private void setPlacement(Map<Integer, ComponentBuilder<?,?>> integerComponentBuilderMap) {
            integerComponentBuilderMap.forEach((slot, componentBuilder) -> {
                componentBuilderPositions.put(componentBuilder, slot);
            });
        }

        @Override
        public <T> Signal<T> useSignal(String s, Class<T> aClass, Supplier<T> defaultValueFunction) {
            if (usedSignals.containsKey(s)) {
                Signal<?> usedSignal = usedSignals.get(s);
                Preconditions.checkState(usedSignal.valueType().equals(aClass), String.format("Failed to use state '%s'! Incompatible types: expected '%s' but got '%s'", s, usedSignal.valueType(), aClass));
                return (Signal<T>) usedSignal;
            }
            Signal<T> signal = new SignalImpl<>(s, aClass, defaultValueFunction);
            usedSignals.put(s, signal);
            return signal;
        }

        @Override
        public Builder renderAt(int i, ItemStackConfig<?> itemStackConfig) {
            if (itemStackConfig instanceof BukkitItemStackConfig bukkitItemStackConfig) {
                this.stackRenderList.put(i, bukkitItemStackConfig);
            }
            return this;
        }

        @Override
        public Builder reactive(SerializableSupplier<Collection<ComponentBuilder<?,?>>> consumer) {
            try {
                // Using serialized lambda we have access to runtime information, such as which outer variables are captured and used inside the lambda.
                // See: https://stackoverflow.com/a/35223119
                SerializedLambda s = getSerializedLambda(consumer);
                ArrayList<Signal<?>> signals = new ArrayList<>(s.getCapturedArgCount());
                for (int i = 0; i < s.getCapturedArgCount(); i++) {
                    if (s.getCapturedArg(i) instanceof Signal<?> signal) {
                        signals.add(signal);
                    }
                }
                ReactiveFunction reactiveFunction = new ReactiveFunction(signals, consumer);
                reactiveFunctions.add(reactiveFunction);
            } catch (Exception e) {
                wolfyUtils.getLogger().severe("Failed to initiate reactive function!");
                e.printStackTrace();
            }
            return this;
        }

        SerializedLambda getSerializedLambda(Serializable lambda) throws Exception {
            final Method method = lambda.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            return (SerializedLambda) method.invoke(lambda);
        }

        @Override
        public <B extends ComponentBuilder<? extends Component, Component>> B create(int slot, String id, Class<B> builderType) {
            Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(id, builderType);
            return componentBuilderPositions.keySet().stream()
                    .filter(entry -> entry.getID().equals(id) && entry.getType().equals(builderTypeInfo.getKey()))
                    .findFirst()
                    .map(builderTypeInfo.getValue()::cast)
                    .orElseGet(() -> {
                        Injector injector = Guice.createInjector(Stage.PRODUCTION, binder -> {
                            binder.bind(WolfyUtils.class).toInstance(wolfyUtils);
                            binder.bind(WindowBuilder.class).toInstance(parentBuilder);
                            binder.bind(String.class).toInstance(id);
                        });
                        return injector.getInstance(builderTypeInfo.getValue());
                    });
        }

        @Override
        public <B extends ComponentBuilder<? extends Component, Component>> B extend(String id, Class<B> builderType) {
            Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(id, builderType);
            return builderTypeInfo.getValue().cast(
                    componentBuilderPositions.keySet().stream()
                            .filter(entry -> entry.getID().equals(id) && entry.getType().equals(builderTypeInfo.getKey()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException(String.format("Failed to link to component '%s'! Cannot find existing placement", id)))
            );
        }

        @Override
        public <B extends ComponentBuilder<? extends Component, Component>> Builder position(int slot, String id, Class<B> builderType, Consumer<B> builderConsumer) {
            Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(id, builderType);

            componentBuilderPositions.keySet().stream()
                    .filter(entry -> entry.getID().equals(id) && entry.getType().equals(builderTypeInfo.getKey()))
                    .findFirst()
                    .ifPresentOrElse(entry -> builderConsumer.accept(builderTypeInfo.getValue().cast(entry)), () -> {
                        Injector injector = Guice.createInjector(Stage.PRODUCTION, binder -> {
                            binder.bind(WolfyUtils.class).toInstance(wolfyUtils);
                            binder.bind(WindowBuilder.class).toInstance(parentBuilder);
                            binder.bind(String.class).toInstance(id);
                        });
                        B builder = injector.getInstance(builderTypeInfo.getValue());
                        builderConsumer.accept(builder);
                        componentBuilderPositions.put(builder, slot);
                    });
            return this;
        }

        @Override
        public <B extends ComponentBuilder<? extends Component, Component>> Builder render(String id, Class<B> builderType, Consumer<B> builderConsumer) {
            Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(id, builderType);

            B builder = builderTypeInfo.getValue().cast(
                    componentBuilderPositions.keySet().stream()
                            .filter(entry -> entry.getID().equals(id) && entry.getType().equals(builderTypeInfo.getKey()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException(String.format("Failed to link to component '%s'! Cannot find existing placement", id)))
            );
            builderConsumer.accept(builder);
            componentRenderSet.add(builder);
            return this;
        }

        private <B extends ComponentBuilder<? extends Component, Component>> Pair<NamespacedKey, Class<B>> getBuilderType(String id, Class<B> builderType) {
            RegistryGUIComponentBuilders registry = wolfyUtils.getRegistries().getGuiComponentBuilders();
            NamespacedKey key = registry.getKey(builderType);
            Preconditions.checkArgument(key != null, "Failed to create component '%s'! Cannot find builder '%s' in registry!", id, builderType.getName());
            @SuppressWarnings("unchecked")
            Class<B> builderImplType = (Class<B>) registry.get(key); // We can be sure that the cast is valid, because the key is only non-null if and only if the type matches!
            Preconditions.checkNotNull(builderImplType, "Failed to create component '%s'! Cannot find implementation type of builder '%s' in registry!", id, builderType.getName());
            return new Pair<>(key, builderImplType);
        }

        @Override
        public <B extends ComponentBuilder<? extends Component, Component>> Builder renderAt(int slot, String id, Class<B> builderType, Consumer<B> builderConsumer) {
            Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(id, builderType);
            componentBuilderPositions.keySet().stream()
                    .filter(entry -> entry.getID().equals(id) && entry.getType().equals(builderTypeInfo.getKey()))
                    .findFirst()
                    .ifPresentOrElse(entry -> builderConsumer.accept(builderTypeInfo.getValue().cast(entry)), () -> {
                        Injector injector = Guice.createInjector(Stage.PRODUCTION, binder -> {
                            binder.bind(WolfyUtils.class).toInstance(wolfyUtils);
                            binder.bind(WindowBuilder.class).toInstance(parentBuilder);
                            binder.bind(String.class).toInstance(id);
                        });
                        B builder = injector.getInstance(builderTypeInfo.getValue());
                        builderConsumer.accept(builder);
                        componentBuilderPositions.put(builder, slot);
                        componentRenderSet.add(builder);
                    });
            return this;
        }

        public WindowRenderer create(Window window) {
            Multimap<Component, Integer> finalPostions = ArrayListMultimap.create();
            for (ComponentBuilder<?, ?> componentBuilder : componentRenderSet) {
                Collection<Integer> slots = this.componentBuilderPositions.get(componentBuilder);
                for (Integer slot : slots) {
                    finalPostions.put(componentBuilder.create(null), slot);
                }
            }
            return new WindowRenderer((WindowImpl) window, titleFunction, finalPostions, reactiveFunctions);
        }

        @Override
        public Builder title(net.kyori.adventure.text.Component component) {
            this.titleFunction = new StaticTitleFunction(component);
            return this;
        }

        @Override
        public <S> Builder title(Signal<S> signal, Function<Signal<S>, net.kyori.adventure.text.Component> function) {
            this.titleFunction = new ReactiveTitleFunction<>(signal, function);
            return this;
        }
    }


}
