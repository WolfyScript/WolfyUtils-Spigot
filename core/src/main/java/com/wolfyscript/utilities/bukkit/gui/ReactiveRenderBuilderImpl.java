package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentBuilder;
import com.wolfyscript.utilities.common.gui.Position;
import com.wolfyscript.utilities.common.gui.ReactiveRenderBuilder;
import com.wolfyscript.utilities.tuple.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class ReactiveRenderBuilderImpl implements com.wolfyscript.utilities.common.gui.ReactiveRenderBuilder {

    final WolfyUtils wolfyUtils;
    final Map<ComponentBuilder<?, ?>, Position> componentBuilderPositions = new HashMap<>();
    final Set<ComponentBuilder<?, ?>> toRender = new HashSet<>();

    public ReactiveRenderBuilderImpl(WolfyUtils wolfyUtils, Map<ComponentBuilder<?, ?>, Position> nonRenderedComponents) {
        this.wolfyUtils = wolfyUtils;
        this.componentBuilderPositions.putAll(nonRenderedComponents);
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> ReactiveRenderBuilder.ReactiveResult renderAt(Position position, String id, Class<B> builderType, Consumer<B> builderConsumer) {
        Pair<NamespacedKey, Class<B>> builderTypeInfo = WindowDynamicConstructorImpl.getBuilderType(wolfyUtils, id, builderType);
        B builder = componentBuilderPositions.keySet().stream()
                .filter(entry -> entry.id().equals(id) && entry.getType().equals(builderTypeInfo.getKey()))
                .findFirst()
                .map(builderTypeInfo.getValue()::cast)
                .orElseGet(() -> {
                    Injector injector = Guice.createInjector(Stage.PRODUCTION, binder -> {
                        binder.bind(WolfyUtils.class).toInstance(wolfyUtils);
                        binder.bind(String.class).toInstance(id);
                    });
                    return injector.getInstance(builderTypeInfo.getValue());
                });
        componentBuilderPositions.put(builder, position);
        builderConsumer.accept(builder);

        return new ReactiveResultImpl(builder);
    }

    @Override
    public <B extends ComponentBuilder<? extends Component, Component>> ReactiveRenderBuilder.ReactiveResult render(String id, Class<B> builderType, Consumer<B> builderConsumer) {
        Pair<NamespacedKey, Class<B>> builderTypeInfo = WindowDynamicConstructorImpl.getBuilderType(wolfyUtils, id, builderType);
        B builder = builderTypeInfo.getValue().cast(componentBuilderPositions.keySet().stream()
                .filter(entry -> entry.id().equals(id) && entry.getType().equals(builderTypeInfo.getKey()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Failed to link to component '%s'! Cannot find existing placement", id))));
        builderConsumer.accept(builder);

        return new ReactiveResultImpl(builder);
    }

    public Map<ComponentBuilder<?, ?>, Position> getComponentBuildersToRender() {
        Map<ComponentBuilder<?, ?>, Position> renderComponents = new HashMap<>();
        for (ComponentBuilder<?, ?> componentBuilder : toRender) {
            renderComponents.put(componentBuilder, componentBuilderPositions.get(componentBuilder));
        }
        return renderComponents;
    }

    public static class ReactiveResultImpl implements ReactiveRenderBuilder.ReactiveResult {

        private final ComponentBuilder<?,?> builder;

        ReactiveResultImpl(ComponentBuilder<?,?> builder) {
            this.builder = builder;
        }

        @Override
        public Component construct() {
            return builder.create(null);
        }
    }

}
