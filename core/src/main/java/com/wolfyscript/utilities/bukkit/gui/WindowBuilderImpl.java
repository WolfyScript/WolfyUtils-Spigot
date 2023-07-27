package com.wolfyscript.utilities.bukkit.gui;

import com.fasterxml.jackson.annotation.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.*;
import com.wolfyscript.utilities.common.gui.functions.SerializableConsumer;
import com.wolfyscript.utilities.common.registry.RegistryGUIComponentBuilders;
import com.wolfyscript.utilities.json.annotations.KeyedBaseType;
import com.wolfyscript.utilities.tuple.Pair;

import java.util.*;
import java.util.function.Consumer;

@KeyedStaticId(key = "window")
@KeyedBaseType(baseType = ComponentBuilder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WindowBuilderImpl  implements WindowBuilder {

    private final String id;
    private final WolfyUtils wolfyUtils;
    protected int size;
    protected WindowType type;
    private String staticTitle = null;
    private InteractionCallback interactionCallback = (guiHolder, interactionDetails) -> InteractionResult.def();
    private Consumer<WindowDynamicConstructor> rendererConstructor = builder -> {};
    private final Multimap<ComponentBuilder<?, ?>, Integer> componentBuilderPositions = ArrayListMultimap.create();
    private final Set<ComponentBuilder<?, ?>> componentRenderSet = new HashSet<>();

    @Inject
    @JsonCreator
    protected WindowBuilderImpl(@JsonProperty("id") String windowID,
                                @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils) {
        this.id = windowID;
        this.wolfyUtils = wolfyUtils;
    }

    @JsonSetter("size")
    private void setSize(int size) {
        this.size = size;
    }

    @Override
    public WindowBuilder size(int size) {
        this.size = size;
        return this;
    }

    @JsonSetter("title")
    public void setTitle(String title) {
        this.staticTitle = title;
    }

    @JsonGetter("title")
    public String getStaticTitle() {
        return staticTitle;
    }

    @JsonSetter("inventory_type")
    @Override
    public WindowBuilder type(WindowType type) {
        this.type = type;
        return this;
    }

    @Override
    public WindowBuilder title(String title) {
        this.staticTitle = title;
        return this;
    }

    @JsonSetter("placement")
    private void setPlacement(List<ComponentBuilder<?, ?>> componentBuilders) {
        for (ComponentBuilder<?, ?> componentBuilder : componentBuilders) {
            componentBuilderPositions.putAll(componentBuilder, componentBuilder.getSlots());
        }
    }

    @Override
    public WindowBuilder interact(InteractionCallback interactionCallback) {
        Preconditions.checkNotNull(interactionCallback);
        this.interactionCallback = interactionCallback;
        return this;
    }

    @Override
    public WindowBuilder construct(Consumer<WindowDynamicConstructor> render) {
        Preconditions.checkNotNull(render);
        this.rendererConstructor = render;
        return this;
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> WindowBuilder init(int slot, String id, Class<B> builderType, SerializableConsumer<B> builderConsumer) {
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

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> WindowBuilder render(String id, Class<B> builderType, SerializableConsumer<B> builderConsumer) {
        Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(wolfyUtils, id, builderType);
        B builder = findExistingComponentBuilder(id, builderTypeInfo.getValue(), builderTypeInfo.getKey())
                .orElseThrow(() -> new IllegalStateException(String.format("Failed to link to component '%s'! Cannot find existing placement", id)));
        builderConsumer.accept(builder);
        componentRenderSet.add(builder);
        return this;
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> WindowBuilder renderAt(int slot, String id, Class<B> builderType, SerializableConsumer<B> builderConsumer) {
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

    private <B extends ComponentBuilder<? extends Component, Component>> Optional<B> findExistingComponentBuilder(String id, Class<B> builderImplType, NamespacedKey builderKey) {
        return componentBuilderPositions.keySet().stream()
                .filter(componentBuilder -> componentBuilder.getID().equals(id) && componentBuilder.getType().equals(builderKey))
                .findFirst()
                .map(builderImplType::cast);
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

    @Override
    public Window create(Router parent) {
        Multimap<Component, Integer> staticComponents = ArrayListMultimap.create();
        Multimap<ComponentBuilder<?, ?>, Integer> nonRenderedComponents = ArrayListMultimap.create();

        for (ComponentBuilder<?, ?> componentBuilder : componentBuilderPositions.keySet()) {
            Collection<Integer> slots = componentBuilderPositions.get(componentBuilder);
            if (componentRenderSet.contains(componentBuilder)) {
                staticComponents.putAll(componentBuilder.create(null), slots);
                continue;
            }
            nonRenderedComponents.putAll(componentBuilder, slots);
        }

        return new WindowImpl(
                parent.getID() + "/" + id,
                parent,
                size,
                type,
                staticTitle,
                interactionCallback,
                staticComponents,
                nonRenderedComponents,
                rendererConstructor
        );
    }

}
