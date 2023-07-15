package com.wolfyscript.utilities.bukkit.gui.components;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.gui.AbstractBukkitComponentBuilder;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentBuilder;
import com.wolfyscript.utilities.common.gui.ComponentBuilderSettings;
import com.wolfyscript.utilities.common.gui.components.ComponentCluster;
import com.wolfyscript.utilities.common.gui.components.ComponentClusterBuilder;
import com.wolfyscript.utilities.common.registry.RegistryGUIComponentBuilders;
import com.wolfyscript.utilities.tuple.Pair;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.*;
import java.util.function.Consumer;

@KeyedStaticId(key = "cluster")
@ComponentBuilderSettings(base = ComponentClusterBuilder.class, component = ComponentCluster.class)
public class ComponentClusterBuilderImpl extends AbstractBukkitComponentBuilder<ComponentCluster, Component> implements ComponentClusterBuilder {

    private final Multimap<ComponentBuilder<?, ?>, Integer> componentBuilderPositions = ArrayListMultimap.create();
    private final Set<ComponentBuilder<?, ?>> componentRenderSet = new HashSet<>();

    @JsonCreator
    public ComponentClusterBuilderImpl(@JsonProperty("id") String id, @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils, @JsonProperty("slots") int[] slots) {
        super(id, wolfyUtils, IntList.of(slots));
    }

    @JsonSetter("placement")
    private void setPlacement(List<ComponentBuilder<?, ?>> componentBuilders) {
        for (ComponentBuilder<?, ?> componentBuilder : componentBuilders) {
            componentBuilderPositions.putAll(componentBuilder, componentBuilder.getSlots());
        }
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> ComponentClusterBuilder render(String id, Class<B> builderType, Consumer<B> builderConsumer) {
        Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(getWolfyUtils(), id, builderType);
        B builder = findExistingComponentBuilder(id, builderTypeInfo.getValue(), builderTypeInfo.getKey())
                .orElseThrow(() -> new IllegalStateException(String.format("Failed to link to component '%s'! Cannot find existing placement", id)));
        builderConsumer.accept(builder);
        componentRenderSet.add(builder);
        return this;
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> ComponentClusterBuilder renderAt(int slot, String id, Class<B> builderType, Consumer<B> builderConsumer) {
        Pair<NamespacedKey, Class<B>> builderTypeInfo = getBuilderType(getWolfyUtils(), id, builderType);
        findExistingComponentBuilder(id, builderTypeInfo.getValue(), builderTypeInfo.getKey()).ifPresentOrElse(builderConsumer, () -> {
            Injector injector = Guice.createInjector(Stage.PRODUCTION, binder -> {
                binder.bind(WolfyUtils.class).toInstance(getWolfyUtils());
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

    private <B extends ComponentBuilder<? extends Component, Component>> Optional<B> findExistingComponentBuilder(String id, Class<B> builderImplType, NamespacedKey builderKey) {
        return componentBuilderPositions.keySet().stream()
                .filter(componentBuilder -> componentBuilder.getID().equals(id) && componentBuilder.getType().equals(builderKey))
                .findFirst()
                .map(builderImplType::cast);
    }

    @Override
    public ComponentClusterImpl create(Component component) {
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

        return new ComponentClusterImpl(getID(), getWolfyUtils(), component, getSlots(), staticComponents);
    }
}
