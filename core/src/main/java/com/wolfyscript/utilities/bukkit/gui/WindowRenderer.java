package com.wolfyscript.utilities.bukkit.gui;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreImpl;
import com.wolfyscript.utilities.bukkit.nms.inventory.InventoryUpdate;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.*;
import com.wolfyscript.utilities.common.gui.functions.ReactiveConsumer;
import com.wolfyscript.utilities.common.gui.functions.ReactiveSupplier;
import com.wolfyscript.utilities.common.gui.functions.SerializableConsumer;
import com.wolfyscript.utilities.common.gui.functions.SerializableSupplier;
import com.wolfyscript.utilities.common.registry.RegistryGUIComponentBuilders;
import com.wolfyscript.utilities.tuple.Pair;
import com.wolfyscript.utilities.versioning.MinecraftVersion;
import com.wolfyscript.utilities.versioning.ServerVersion;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class WindowRenderer implements com.wolfyscript.utilities.common.gui.WindowRenderer {

    private final WindowImpl window;
    private final ReactiveSupplier<net.kyori.adventure.text.Component> titleFunction;
    private final Map<String, Signal<?>> signals;
    private final Multimap<Component, Integer> componentPositions = ArrayListMultimap.create();
    private final Multimap<ComponentBuilder<?, ?>, Integer> nonRenderedComponents = ArrayListMultimap.create();
    private final GuiViewManager viewManager;

    public WindowRenderer(WindowImpl window,
                          GuiViewManager viewManager,
                          Map<String, Signal<?>> signals,
                          Multimap<Component, Integer> componentPositions,
                          Multimap<ComponentBuilder<?, ?>, Integer> nonRenderedComponents,
                          ReactiveSupplier<net.kyori.adventure.text.Component> titleFunction) {
        this.window = window;
        this.viewManager = viewManager;
        this.componentPositions.putAll(componentPositions);
        this.nonRenderedComponents.putAll(nonRenderedComponents);
        this.titleFunction = titleFunction;
        this.signals = signals;
    }

    @Override
    public void render(GuiHolder guiHolder, RenderContext context) {
        if (!(context instanceof RenderContextImpl renderContext)) return;

        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0))) {
            ((GUIHolder) guiHolder).getBukkitPlayer().getOpenInventory().setTitle(BukkitComponentSerializer.legacy().serialize(titleFunction.get()));
        } else {
            InventoryUpdate.updateInventory(((WolfyCoreImpl) window.getWolfyUtils().getCore()).getWolfyUtils().getPlugin(), ((GUIHolder) guiHolder).getBukkitPlayer(), titleFunction.get());
        }

        for (Map.Entry<Component, Integer> entry : componentPositions.entries()) {
            int slot = entry.getValue();
            Component component = entry.getKey();
            entry.getKey().executeForAllSlots(slot, slot2 -> ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(component, slot2));
            renderContext.setSlotOffsetToParent(slot);
            renderContext.enterNode(component);
            component.construct(viewManager).render(guiHolder, renderContext);
            renderContext.exitNode();
        }
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
    public Map<String, Signal<?>> getSignals() {
        return signals;
    }

    @Override
    public NativeRendererModule getNativeModule() {
        return null;
    }

    public static class Builder implements com.wolfyscript.utilities.common.gui.WindowRenderer.Builder {

        final WolfyUtils wolfyUtils;
        final GuiViewManager viewManager;
        final WindowImpl window;
        final Multimap<ComponentBuilder<?, ?>, Integer> componentBuilderPositions = ArrayListMultimap.create();
        final Set<ComponentBuilder<?, ?>> componentRenderSet = new HashSet<>();
        final Map<String, Signal<?>> usedSignals = new HashMap<>();
        final Set<ReactiveConsumer<com.wolfyscript.utilities.common.gui.WindowRenderer.ReactiveRenderBuilder>> reactiveFunctions = new HashSet<>();
        private final List<TagResolver> titleTagResolvers = new ArrayList<>();
        private ReactiveSupplier<net.kyori.adventure.text.Component> titleFunction;
        private final Set<Signal<?>> titleSignals = new HashSet<>();

        @JsonCreator
        public Builder(@JacksonInject WolfyUtils wolfyUtils, GuiViewManager viewManager, WindowImpl window) {
            this.wolfyUtils = wolfyUtils;
            this.window = window;
            this.viewManager = viewManager;

            this.componentBuilderPositions.putAll(window.nonRenderedComponents);
        }

        @Override
        public Builder title(SerializableSupplier<net.kyori.adventure.text.Component> titleSupplier) {
            this.titleFunction = new ReactiveSupplier<>(titleSupplier);
            return this;
        }

        @Override
        public Builder titleSignals(Signal<?>... signals) {
            titleTagResolvers.addAll(Arrays.stream(signals)
                    .map(signal -> TagResolver.resolver(signal.key(), (argumentQueue, context) -> Tag.inserting(net.kyori.adventure.text.Component.text(String.valueOf(signal.get())))))
                    .toList());
            titleSignals.addAll(Arrays.stream(signals).toList());
            return this;
        }

        @JsonSetter("placement")
        private void setPlacement(List<ComponentBuilder<?, ?>> componentBuilders) {
            for (ComponentBuilder<?, ?> componentBuilder : componentBuilders) {
                componentBuilderPositions.putAll(componentBuilder, componentBuilder.getSlots());
            }
        }

        @Override
        public <T> Signal<T> createSignal(String s, Class<T> aClass, Supplier<T> defaultValueFunction) {
            if (usedSignals.containsKey(s)) {
                Signal<?> usedSignal = usedSignals.get(s);
                Preconditions.checkState(usedSignal.valueType().equals(aClass), String.format("Failed to use existing state '%s'! Incompatible types: expected '%s' but got '%s'", s, usedSignal.valueType(), aClass));
                return (Signal<T>) usedSignal;
            }
            var signal = new SignalImpl<>(s, viewManager, aClass, defaultValueFunction);
            usedSignals.put(s, signal);
            return signal;
        }

        @Override
        public Builder reactive(SerializableConsumer<com.wolfyscript.utilities.common.gui.WindowRenderer.ReactiveRenderBuilder> consumer) {
            SignalledObject signalledObject = new SignalledObject() {

                private Set<Component> previousComponents = new HashSet<>();

                @Override
                public void update(GuiViewManager guiViewManager, GuiHolder guiHolder, RenderContext context) {
                    if (!(context instanceof RenderContextImpl renderContext)) return;
                    ReactiveRenderBuilderImpl builder = new ReactiveRenderBuilderImpl(wolfyUtils, componentBuilderPositions);
                    consumer.accept(builder);

                    final Set<Component> freshComponents = builder.getComponentBuildersToRender().keySet().stream().map(componentBuilder -> componentBuilder.create(null)).collect(Collectors.toSet());

                    for (Component component : Sets.difference(previousComponents, freshComponents)) {
                        for (int slot : component.getSlots()) {
                            component.executeForAllSlots(slot, slot2 -> {
                                renderContext.setNativeStack(slot2, null);
                                ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(null, slot2);
                            });
                        }
                    }

                    for (Component component : freshComponents) {
                        renderContext.enterNode(component);
                        for (int slot : component.getSlots()) {
                            renderContext.setSlotOffsetToParent(slot);
                            component.construct(guiViewManager).render(guiHolder, renderContext);
                            component.executeForAllSlots(slot, slot2 -> ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(component, slot2));
                        }
                        renderContext.exitNode();
                    }

                    previousComponents = freshComponents;
                }
            };
            for (Signal<?> signal : consumer.getSignalsUsed()) {
                ((SignalImpl<?>) signal).linkTo(signalledObject);
            }
            return this;
        }

        @Override
        public <B extends ComponentBuilder<? extends Component, Component>> Builder ifThenRender(SerializableSupplier<Boolean> condition, String id, Class<B> builderType, SerializableConsumer<B> builderConsumer) {
            Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(wolfyUtils, id, builderType);
            B builder = findExistingComponentBuilder(id, builderTypeInfo.getValue(), builderTypeInfo.getKey())
                    .orElseThrow(() -> new IllegalStateException(String.format("Failed to link to component '%s'! Cannot find existing placement", id)));
            builderConsumer.accept(builder);

            Component component = builder.create(null);

            SignalledObject signalledObject = new SignalledObject() {

                private boolean previousResult = false;

                @Override
                public void update(GuiViewManager guiViewManager, GuiHolder guiHolder, RenderContext context) {
                    if (!(context instanceof RenderContextImpl renderContext)) return;
                    boolean result = condition.get();
                    if (result != previousResult) {
                        previousResult = result;
                        if (result) {
                            renderContext.enterNode(component);
                            for (int slot : component.getSlots()) {
                                renderContext.setSlotOffsetToParent(slot);
                                component.construct(guiViewManager).render(guiHolder, renderContext);
                                component.executeForAllSlots(slot, slot2 -> ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(component, slot2));
                            }
                            renderContext.exitNode();
                        } else {
                            for (int slot : component.getSlots()) {
                                component.executeForAllSlots(slot, slot2 -> {
                                    renderContext.setNativeStack(slot2, null);
                                    ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(null, slot2);
                                });
                            }
                        }
                    }
                }
            };

            for (Signal<?> signal : condition.getSignalsUsed()) {
                ((SignalImpl<?>) signal).linkTo(signalledObject);
            }
            return this;
        }

        @Override
        public <BV extends ComponentBuilder<? extends Component, Component>, BI extends ComponentBuilder<? extends Component, Component>> Builder ifThenRenderOr(SerializableSupplier<Boolean> serializableSupplier, Class<BV> validBuilderType, Consumer<BV> validBuilder, Class<BI> invalidBuilderType, SerializableConsumer<BI> invalidBuilder) {
            return null;
        }

        @Override
        public <B extends ComponentBuilder<? extends Component, Component>> Builder position(int slot, String id, Class<B> builderType, SerializableConsumer<B> builderConsumer) {
            Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(wolfyUtils, id, builderType);

            findExistingComponentBuilder(id, builderTypeInfo.getValue(), builderTypeInfo.getKey()).ifPresentOrElse(builderConsumer, () -> {
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

        private <B extends ComponentBuilder<? extends Component, Component>> Optional<B> findExistingComponentBuilder(String id, Class<B> builderImplType, NamespacedKey builderKey) {
            return componentBuilderPositions.keySet().stream()
                    .filter(componentBuilder -> componentBuilder.getID().equals(id) && componentBuilder.getType().equals(builderKey))
                    .findFirst()
                    .map(builderImplType::cast);
        }

        @Override
        public <B extends ComponentBuilder<? extends Component, Component>> Builder render(String id, Class<B> builderType, SerializableConsumer<B> builderConsumer) {
            Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(wolfyUtils, id, builderType);
            B builder = findExistingComponentBuilder(id, builderTypeInfo.getValue(), builderTypeInfo.getKey())
                    .orElseThrow(() -> new IllegalStateException(String.format("Failed to link to component '%s'! Cannot find existing placement", id)));
            builderConsumer.accept(builder);
            componentRenderSet.add(builder);
            return this;
        }

        @Override
        public <B extends ComponentBuilder<? extends Component, Component>> Builder renderAt(int slot, String id, Class<B> builderType, SerializableConsumer<B> builderConsumer) {
            Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(wolfyUtils, id, builderType);
            findExistingComponentBuilder(id, builderTypeInfo.getValue(), builderTypeInfo.getKey()).ifPresentOrElse(builderConsumer, () -> {
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

            for (var entry : componentBuilderPositions.asMap().entrySet()) {
                ComponentBuilder<?,?> componentBuilder = entry.getKey();
                Collection<Integer> slots = entry.getValue();
                if (componentRenderSet.contains(componentBuilder)) {
                    Component component = componentBuilder.create(null);
                    finalPostions.putAll(component, slots);
                    continue;
                }
                nonRenderedComponents.putAll(componentBuilder, slots);
            }

            if (titleFunction == null) {
                titleFunction = new ReactiveSupplier<>(() -> wolfyUtils.getChat().getMiniMessage().deserialize(((WindowImpl) window).getStaticTitle(), TagResolver.resolver(titleTagResolvers)));
            }
            SignalledObject signalledObject = (viewManager, guiHolder, renderContext) -> {
                if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0))) {
                    ((GUIHolder) guiHolder).getBukkitPlayer().getOpenInventory().setTitle(BukkitComponentSerializer.legacy().serialize(titleFunction.get()));
                } else {
                    InventoryUpdate.updateInventory(((WolfyCoreImpl) viewManager.getWolfyUtils().getCore()).getWolfyUtils().getPlugin(), ((GUIHolder) guiHolder).getBukkitPlayer(), titleFunction.get());
                }
            };
            for (Signal<?> signal : titleFunction.signals()) {
                ((SignalImpl<?>) signal).linkTo(signalledObject);
            }
            for (Signal<?> signal : titleSignals) {
                ((SignalImpl<?>) signal).linkTo(signalledObject);
            }

            return new WindowRenderer((WindowImpl) window, viewManager, usedSignals, finalPostions, nonRenderedComponents, titleFunction);
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
