package com.wolfyscript.utilities.bukkit.gui;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
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
import com.wolfyscript.utilities.common.gui.WindowState;
import com.wolfyscript.utilities.common.gui.functions.ReactiveConsumer;
import com.wolfyscript.utilities.common.gui.functions.ReactiveSupplier;
import com.wolfyscript.utilities.common.gui.functions.SerializableConsumer;
import com.wolfyscript.utilities.common.gui.functions.SerializableSupplier;
import com.wolfyscript.utilities.common.registry.RegistryGUIComponentBuilders;
import com.wolfyscript.utilities.tuple.Pair;
import com.wolfyscript.utilities.versioning.MinecraftVersion;
import com.wolfyscript.utilities.versioning.ServerVersion;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class WindowRenderer implements com.wolfyscript.utilities.common.gui.WindowRenderer {

    private final WindowImpl window;
    private final Multimap<Component, Integer> componentPositions = ArrayListMultimap.create();
    private final Set<ReactiveConsumer<com.wolfyscript.utilities.common.gui.WindowRenderer.ReactiveRenderBuilder>> reactiveFunctions;
    private final ReactiveSupplier<net.kyori.adventure.text.Component> titleFunction;
    private final Map<String, Signal<?>> signals;
    private final Multimap<ComponentBuilder<?, ?>, Integer> nonRenderedComponents = ArrayListMultimap.create();

    public WindowRenderer(WindowImpl window,
                          Map<String, Signal<?>> signals,
                          Multimap<Component, Integer> componentPositions,
                          Multimap<ComponentBuilder<?, ?>, Integer> nonRenderedComponents,
                          ReactiveSupplier<net.kyori.adventure.text.Component> titleFunction,
                          Set<ReactiveConsumer<ReactiveRenderBuilder>> reactiveFunctions) {
        this.window = window;
        this.componentPositions.putAll(componentPositions);
        this.reactiveFunctions = reactiveFunctions;
        this.nonRenderedComponents.putAll(nonRenderedComponents);
        this.titleFunction = titleFunction;
        this.signals = signals;
    }

    @Override
    public void render(WindowState state, GuiHolder guiHolder, RenderContext renderContext) {
        if (!(state instanceof WindowStateImpl windowState)) return;
        signals.forEach((s, signal) -> signal.enter(guiHolder.getViewManager()));

        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0))) {
            ((GUIHolder) guiHolder).getPlayer().getOpenInventory().setTitle(BukkitComponentSerializer.legacy().serialize(titleFunction.get()));
        } else {
            InventoryUpdate.updateInventory(((WolfyCoreImpl) window.getWolfyUtils().getCore()).getWolfyUtils().getPlugin(), ((GUIHolder) guiHolder).getPlayer(), titleFunction.get());
        }

        for (Map.Entry<Component, Integer> entry : componentPositions.entries()) {
            updateComponent(windowState, entry.getValue(), entry.getKey()).ifPresent(state1 -> windowState.updateComponent(entry.getValue(), state1));
        }

        if (!reactiveFunctions.isEmpty()) {
            Map<Integer, ComponentStateImpl<?, ?>> reactiveUpdateQueue = new HashMap<>();

            for (ReactiveConsumer<com.wolfyscript.utilities.common.gui.WindowRenderer.ReactiveRenderBuilder> reactiveFunction : reactiveFunctions) {
                int functionId = reactiveFunction.id();
                reactiveUpdateQueue.clear();
                var reactiveBoundStates = windowState.reactiveBoundStates.computeIfAbsent(functionId, integer -> new Int2ObjectOpenHashMap<>());

                if (reactiveFunction.signals().stream().anyMatch(signal -> windowState.updatedSignals().contains(signal))) {
                    var reactiveBuilder = new ReactiveRenderBuilderImpl(window.getWolfyUtils(), nonRenderedComponents);
                    reactiveFunction.accept(reactiveBuilder);
                    reactiveBuilder.getComponentBuildersToRender().forEach((componentBuilder, slot) -> {
                        updateReactiveComponent(windowState, functionId, slot, componentBuilder.create(null)).ifPresent(componentState -> reactiveUpdateQueue.put(slot, componentState));
                    });
                    for (var entry : reactiveBoundStates.entrySet()) {
                        ComponentStateImpl<?,?> existingState = entry.getValue();
                        int slot = entry.getKey();
                        if (reactiveUpdateQueue.containsKey(slot) && isSameComponent(reactiveUpdateQueue.get(slot), existingState.getOwner().getID())) continue;
                        existingState.getOwner().executeForAllSlots(slot, slot2 -> {
                            ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(null, slot2);
                            ((RenderContextImpl) renderContext).getInventory().setItem(slot2, null);
                        });
                    }
                    reactiveBoundStates.clear();
                    for (var entry : reactiveUpdateQueue.entrySet()) {
                        ComponentStateImpl<?,?> childState = entry.getValue();
                        int slot = entry.getKey();
                        childState.getOwner().executeForAllSlots(slot, slot2 -> ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(childState, slot2));
                        ((RenderContextImpl) renderContext).setSlotOffsetToParent(slot);
                        ((RenderContextImpl) renderContext).enterNode(guiHolder.getViewManager(), childState);
                        windowState.updateReactiveComponent(functionId, slot, childState);
                        renderState(childState, guiHolder, renderContext);
                        ((RenderContextImpl) renderContext).exitNode();
                    }
                } else {
                    for (var entry : reactiveBoundStates.entrySet()) {
                        ComponentStateImpl<?,?> childState = entry.getValue();
                        int slot = entry.getKey();
                        childState.getOwner().executeForAllSlots(slot, slot2 -> ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(childState, slot2));
                        ((RenderContextImpl) renderContext).setSlotOffsetToParent(slot);
                        ((RenderContextImpl) renderContext).enterNode(guiHolder.getViewManager(), childState);
                        renderState(childState, guiHolder, renderContext);
                        ((RenderContextImpl) renderContext).exitNode();
                    }
                }
            }
        }

        // Free up unused space/slots
        windowState.updatedSignals().clear();

        windowState.childComponentStates.forEach((slot, childState) -> {
            childState.getOwner().executeForAllSlots(slot, slot2 -> ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(childState, slot2));
            ((RenderContextImpl) renderContext).setSlotOffsetToParent(slot);
            ((RenderContextImpl) renderContext).enterNode(guiHolder.getViewManager(), childState);
            windowState.updateComponent(slot, childState);
            renderState(childState, guiHolder, renderContext);
            ((RenderContextImpl) renderContext).exitNode();
        });

        signals.forEach((s, signal) -> signal.exit());
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

    public Optional<ComponentStateImpl<?, ?>> updateComponent(WindowState state, int slot, Component component) {
        if (!(state instanceof WindowStateImpl windowState)) return Optional.empty();
        return Optional.ofNullable((ComponentStateImpl<?, ?>) state.get(slot)
                .filter(existingState -> isSameComponent(existingState, component.getID()))
                .orElseGet(() -> {
                    if (checkBoundsAtPos(slot, component)) {
                        if (component instanceof Stateful<?> stateful) {
                            return stateful.createState(null, windowState.viewManager);
                        }
                    } else {
                        throw new IllegalArgumentException("Component does not fit inside of the Window!");
                    }
                    return null;
                }));
    }

    public Optional<ComponentStateImpl<?, ?>> updateReactiveComponent(WindowState state, int functionId, int slot, Component component) {
        if (!(state instanceof WindowStateImpl windowState)) return Optional.empty();
        ComponentStateImpl<?,?> existingState = ((WindowStateImpl) state).reactiveBoundStates.computeIfAbsent(functionId, integer -> new Int2ObjectOpenHashMap<>()).get(slot);
        if (existingState == null || isSameComponent(existingState, component.getID())) {
            if (checkBoundsAtPos(slot, component)) {
                if (component instanceof Stateful<?> stateful) {
                    return Optional.ofNullable((ComponentStateImpl<?, ?>) stateful.createState(null, windowState.viewManager));
                }
            } else {
                throw new IllegalArgumentException("Component does not fit inside of the Window!");
            }
        }
        return Optional.empty();
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

    public static class Builder implements com.wolfyscript.utilities.common.gui.WindowRenderer.Builder {

        final WolfyUtils wolfyUtils;
        final Multimap<ComponentBuilder<?, ?>, Integer> componentBuilderPositions = ArrayListMultimap.create();
        final Set<ComponentBuilder<?, ?>> componentRenderSet = new HashSet<>();
        final Map<String, Signal<?>> usedSignals = new HashMap<>();
        final Set<ReactiveConsumer<com.wolfyscript.utilities.common.gui.WindowRenderer.ReactiveRenderBuilder>> reactiveFunctions = new HashSet<>();

        private final List<TagResolver> titleTagResolvers = new ArrayList<>();
        private String staticTitle;
        private ReactiveSupplier<net.kyori.adventure.text.Component> titleFunction = null;

        @JsonCreator
        public Builder(@JacksonInject WolfyUtils wolfyUtils) {
            this.wolfyUtils = wolfyUtils;
        }

        @JsonSetter("placement")
        private void setPlacement(Map<Integer, ComponentBuilder<?, ?>> integerComponentBuilderMap) {
            integerComponentBuilderMap.forEach((slot, componentBuilder) -> componentBuilderPositions.put(componentBuilder, slot));
        }

        @JsonSetter("title")
        private void setTitle(String title) {
            this.staticTitle = title;
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
        public Builder reactive(SerializableConsumer<com.wolfyscript.utilities.common.gui.WindowRenderer.ReactiveRenderBuilder> consumer) {
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
                ReactiveConsumer<com.wolfyscript.utilities.common.gui.WindowRenderer.ReactiveRenderBuilder> reactiveConsumer = new ReactiveConsumer<>(signals, consumer);
                reactiveFunctions.add(reactiveConsumer);
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
        public <B extends ComponentBuilder<? extends Component, Component>> Builder position(int slot, String id, Class<B> builderType, Consumer<B> builderConsumer) {
            Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(wolfyUtils, id, builderType);

            componentBuilderPositions.keySet().stream()
                    .filter(entry -> entry.getID().equals(id) && entry.getType().equals(builderTypeInfo.getKey()))
                    .findFirst()
                    .ifPresentOrElse(entry -> builderConsumer.accept(builderTypeInfo.getValue().cast(entry)), () -> {
                        Injector injector = Guice.createInjector(Stage.PRODUCTION, binder -> {
                            binder.bind(WolfyUtils.class).toInstance(wolfyUtils);
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
            Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(wolfyUtils, id, builderType);

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

        @Override
        public <B extends ComponentBuilder<? extends Component, Component>> Builder renderAt(int slot, String id, Class<B> builderType, Consumer<B> builderConsumer) {
            Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(wolfyUtils, id, builderType);
            componentBuilderPositions.keySet().stream()
                    .filter(entry -> entry.getID().equals(id) && entry.getType().equals(builderTypeInfo.getKey()))
                    .findFirst()
                    .ifPresentOrElse(entry -> builderConsumer.accept(builderTypeInfo.getValue().cast(entry)), () -> {
                        Injector injector = Guice.createInjector(Stage.PRODUCTION, binder -> {
                            binder.bind(WolfyUtils.class).toInstance(wolfyUtils);
                            binder.bind(String.class).toInstance(id);
                        });
                        B builder = injector.getInstance(builderTypeInfo.getValue());
                        builderConsumer.accept(builder);
                        componentBuilderPositions.put(builder, slot);
                        componentRenderSet.add(builder);
                    });
            return this;
        }

        @Override
        public Builder title(net.kyori.adventure.text.Component textComponent) {
            title(() -> textComponent);
            return this;
        }

        @Override
        public Builder title(String staticTitle) {
            this.staticTitle = staticTitle;
            return this;
        }

        @Override
        public Builder titleSignals(Signal<?>... signals) {
            titleTagResolvers.addAll(Arrays.stream(signals)
                    .map(signal -> TagResolver.resolver(signal.key(), (argumentQueue, context) -> Tag.inserting(net.kyori.adventure.text.Component.text(String.valueOf(signal.get())))))
                    .toList());
            return this;
        }

        @Override
        public Builder title(SerializableSupplier<net.kyori.adventure.text.Component> titleSupplier) {
            try {
                // Using serialized lambda we have access to runtime information, such as which outer variables are captured and used inside the lambda.
                // See: https://stackoverflow.com/a/35223119
                SerializedLambda s = getSerializedLambda(titleSupplier);
                ArrayList<Signal<?>> signals = new ArrayList<>(s.getCapturedArgCount());
                for (int i = 0; i < s.getCapturedArgCount(); i++) {
                    if (s.getCapturedArg(i) instanceof Signal<?> signal) {
                        signals.add(signal);
                    }
                }
                this.titleFunction = new ReactiveSupplier<>(signals, titleSupplier);
            } catch (Exception e) {
                wolfyUtils.getLogger().severe("Failed to initiate reactive function!");
                e.printStackTrace();
            }
            return this;
        }

        private static <B extends ComponentBuilder<? extends Component, Component>> Pair<NamespacedKey, Class<B>> getBuilderType(WolfyUtils wolfyUtils, String id, Class<B> builderType) {
            RegistryGUIComponentBuilders registry = wolfyUtils.getRegistries().getGuiComponentBuilders();
            NamespacedKey key = registry.getKey(builderType);
            Preconditions.checkArgument(key != null, "Failed to create component '%s'! Cannot find builder '%s' in registry!", id, builderType.getName());
            @SuppressWarnings("unchecked")
            Class<B> builderImplType = (Class<B>) registry.get(key); // We can be sure that the cast is valid, because the key is only non-null if and only if the type matches!
            Preconditions.checkNotNull(builderImplType, "Failed to create component '%s'! Cannot find implementation type of builder '%s' in registry!", id, builderType.getName());
            return new Pair<>(key, builderImplType);
        }

        public WindowRenderer create(Window window) {
            Multimap<Component, Integer> finalPostions = ArrayListMultimap.create();
            Multimap<ComponentBuilder<?, ?>, Integer> nonRenderedComponents = ArrayListMultimap.create();

            for (ComponentBuilder<?, ?> componentBuilder : componentBuilderPositions.keySet()) {
                Collection<Integer> slots = componentBuilderPositions.get(componentBuilder);
                if (componentRenderSet.contains(componentBuilder)) {
                    finalPostions.putAll(componentBuilder.create(null), slots);
                    continue;
                }
                nonRenderedComponents.putAll(componentBuilder, slots);
            }

            if (titleFunction == null) {
                if (staticTitle != null) {
                    final TagResolver tagResolvers = TagResolver.resolver(titleTagResolvers);
                    final String finalTitle = staticTitle;
                    title(() -> wolfyUtils.getChat().getMiniMessage().deserialize(finalTitle, tagResolvers));
                } else {
                    title(net.kyori.adventure.text.Component::empty);
                }
            }

            return new WindowRenderer((WindowImpl) window, usedSignals, finalPostions, nonRenderedComponents, titleFunction, reactiveFunctions);
        }

    }

    public static class ReactiveRenderBuilderImpl implements com.wolfyscript.utilities.common.gui.WindowRenderer.ReactiveRenderBuilder {

        final WolfyUtils wolfyUtils;
        final Multimap<ComponentBuilder<?, ?>, Integer> componentBuilderPositions = ArrayListMultimap.create();
        final Set<ComponentBuilder<?, ?>> toRender = new HashSet<>();

        public ReactiveRenderBuilderImpl(WolfyUtils wolfyUtils, Multimap<ComponentBuilder<?, ?>, Integer> nonRenderedComponents) {
            this.wolfyUtils = wolfyUtils;
            this.componentBuilderPositions.putAll(nonRenderedComponents);
        }

        @Override
        public <B extends ComponentBuilder<? extends Component, Component>> ReactiveRenderBuilderImpl renderAt(int slot, String id, Class<B> builderType, Consumer<B> builderConsumer) {
            Pair<NamespacedKey, Class<B>> builderTypeInfo = WindowRenderer.Builder.getBuilderType(wolfyUtils, id, builderType);
            B builder = componentBuilderPositions.keySet().stream()
                    .filter(entry -> entry.getID().equals(id) && entry.getType().equals(builderTypeInfo.getKey()))
                    .findFirst()
                    .map(builderTypeInfo.getValue()::cast)
                    .orElseGet(() -> {
                        Injector injector = Guice.createInjector(Stage.PRODUCTION, binder -> {
                            binder.bind(WolfyUtils.class).toInstance(wolfyUtils);
                            binder.bind(String.class).toInstance(id);
                        });
                        return injector.getInstance(builderTypeInfo.getValue());
                    });
            componentBuilderPositions.put(builder, slot);
            builderConsumer.accept(builder);
            toRender.add(builder);
            return this;
        }

        @Override
        public <B extends ComponentBuilder<? extends Component, Component>> ReactiveRenderBuilderImpl render(String id, Class<B> builderType, Consumer<B> builderConsumer) {
            Pair<NamespacedKey, Class<B>> builderTypeInfo = WindowRenderer.Builder.getBuilderType(wolfyUtils, id, builderType);
            B builder = builderTypeInfo.getValue().cast(componentBuilderPositions.keys().stream()
                    .filter(entry -> entry.getID().equals(id) && entry.getType().equals(builderTypeInfo.getKey()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(String.format("Failed to link to component '%s'! Cannot find existing placement", id))));
            builderConsumer.accept(builder);
            toRender.add(builder);
            return this;
        }

        public Multimap<ComponentBuilder<?, ?>, Integer> getComponentBuildersToRender() {
            Multimap<ComponentBuilder<?, ?>, Integer> renderComponents = ArrayListMultimap.create();
            for (ComponentBuilder<?, ?> componentBuilder : toRender) {
                renderComponents.putAll(componentBuilder, componentBuilderPositions.get(componentBuilder));
            }
            return renderComponents;
        }

    }


}