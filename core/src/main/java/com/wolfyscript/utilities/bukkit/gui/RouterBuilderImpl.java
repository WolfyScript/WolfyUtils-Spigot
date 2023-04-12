package com.wolfyscript.utilities.bukkit.gui;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.ComponentBuilderSettings;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.MenuComponent;
import com.wolfyscript.utilities.common.gui.Router;
import com.wolfyscript.utilities.common.gui.RouterBuilder;
import com.wolfyscript.utilities.common.gui.RouterChildBuilder;
import com.wolfyscript.utilities.common.gui.RouterEntry;
import com.wolfyscript.utilities.common.gui.RouterEntryBuilder;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.Window;
import com.wolfyscript.utilities.common.gui.WindowBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@KeyedStaticId(key = "router")
@ComponentBuilderSettings(base = RouterBuilder.class, component = Router.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class RouterBuilderImpl extends AbstractBukkitComponentBuilder<Router, Router> implements RouterBuilder {

    private final RouterBuilderImpl parent;
    private final ChildBuilder childComponentBuilder;
    private final RouterEntryBuilderImpl routerEntryBuilder = new RouterEntryBuilderImpl();
    private InteractionCallback interactionCallback = (guiHolder, componentState, interactionDetails) -> InteractionResult.def();
    private final Map<String, Signal.Builder<?>> signals;

    @Inject
    @JsonCreator
    RouterBuilderImpl(@JsonProperty("id") String routerID, @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils, @JacksonInject("parent") RouterBuilderImpl parent) {
        super(routerID, wolfyUtils);
        this.parent = parent;
        this.childComponentBuilder = new ChildBuilder();
        this.signals = new HashMap<>();
    }

    @JsonSetter("signals")
    private void setSignals(List<Signal.Builder<?>> signalBuilders) {
        for (Signal.Builder<?> signalBuilder : signalBuilders) {
            signals.put(signalBuilder.getKey(), signalBuilder);
        }
    }

    @Override
    public <T> RouterBuilder useSignal(String key, Class<T> type, Consumer<Signal.Builder<T>> signalBuilder) {
        if (signals.containsKey(key)) {
            Signal.Builder<?> builder = signals.get(key);
            if (builder.getValueType().equals(type)) {
                signalBuilder.accept((Signal.Builder<T>) builder);
                return this;
            }
            throw new IllegalStateException("A Signal already exists for a different value type!");
        }
        SignalImpl.Builder<T> builder = new SignalImpl.Builder<>(key, type);
        signalBuilder.accept(builder);
        this.signals.put(key, builder);
        return this;
    }

    @Override
    public RouterBuilder children(Consumer<RouterChildBuilder> childComponentBuilderConsumer) {
        Preconditions.checkArgument(childComponentBuilderConsumer != null);
        childComponentBuilderConsumer.accept(this.childComponentBuilder);
        return this;
    }

    @Override
    public RouterBuilder entry(Consumer<RouterEntryBuilder> entryBuilder) {
        Preconditions.checkArgument(entryBuilder != null);
        entryBuilder.accept(routerEntryBuilder);
        Preconditions.checkState(routerEntryBuilder.id != null && routerEntryBuilder.type != null, "Invalid Entry! Please make sure you provide a valid id!");
        return this;
    }

    @Override
    public RouterBuilder interact(InteractionCallback interactionCallback) {
        Preconditions.checkArgument(interactionCallback != null);
        this.interactionCallback = interactionCallback;
        return this;
    }

    @Override
    public Router create(Router parent) {
        RouterImpl router = new RouterImpl(
                getID(),
                getWolfyUtils(),
                parent,
                signals.values().stream().map(Signal.Builder::create).collect(Collectors.toMap(Signal::key, Function.identity())),
                interactionCallback
        );
        childComponentBuilder.applyTo(router);
        Preconditions.checkState(!router.childComponents().isEmpty() || router.childRoutes().isEmpty(), "Cannot create Router without child Components and Routes!");
        router.setEntry(routerEntryBuilder.build(router));
        return router;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class RouterEntryBuilderImpl implements RouterEntryBuilder {

        private String id;
        private RouterEntry.Type type;

        @JsonCreator
        RouterEntryBuilderImpl() {
            this.id = null;
            this.type = null;
        }

        @JsonSetter("id")
        private void setId(String id) {
            this.id = id;
        }

        @JsonSetter("type")
        private void setType(RouterEntry.Type type) {
            this.type = type;
        }

        @Override
        public RouterEntryBuilderImpl window(String id) {
            this.id = id;
            this.type = RouterEntry.Type.WINDOW;
            return this;
        }

        @Override
        public RouterEntryBuilderImpl route(String id) {
            this.id = id;
            this.type = RouterEntry.Type.ROUTER;
            return this;
        }

        RouterEntry build(RouterImpl router) {
            if (type == null) {
                return router.childComponents().stream().findFirst()
                        .map(window -> new RouterEntryImpl(window, RouterEntry.Type.WINDOW))
                        .or(() -> router.childRoutes().stream().findFirst().map(router1 -> new RouterEntryImpl(router1, RouterEntry.Type.ROUTER)))
                        .orElseThrow(() -> new IllegalStateException("Cannot automatically determine an Entry of Router: " + router.getID()));
            }
            return switch (type) {
                case WINDOW ->
                        router.getChild(id).map(window -> new RouterEntryImpl(window, type))
                                .orElseThrow(() -> new IllegalStateException("Cannot find specified Window Entry '" + id + "' of Router: " + router.getID()));
                case ROUTER ->
                        router.getRoute(id).map(router1 -> new RouterEntryImpl(router1, type))
                                .orElseThrow(() -> new IllegalStateException("Cannot find specified Router Entry '" + id + "' of Router: " + router.getID()));
            };
        }


    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    final class ChildBuilder implements RouterChildBuilder {

        private final List<WindowBuilder> windowComponentBuilders = new ArrayList<>();
        private final List<RouterBuilder> routerBuilders = new ArrayList<>();

        @JsonCreator
        ChildBuilder() {
        }

        @Override
        public RouterChildBuilder window(String id, Consumer<WindowBuilder> windowComponentBuilderConsumer) {
            var windowBuilder = new WindowBuilderImpl(id, getWolfyUtils(), RouterBuilderImpl.this);
            windowComponentBuilderConsumer.accept(windowBuilder);
            windowComponentBuilders.add(windowBuilder);
            return this;
        }

        @Override
        public RouterChildBuilder router(String id, Consumer<RouterBuilder> clusterComponentBuilderConsumer) {
            RouterBuilder clusterBuilder = new RouterBuilderImpl(id, getWolfyUtils(), RouterBuilderImpl.this);
            clusterComponentBuilderConsumer.accept(clusterBuilder);
            routerBuilders.add(clusterBuilder);
            return this;
        }

        @Override
        public void applyTo(Router router) {
            if (!(router instanceof RouterImpl parentRouter)) return;
            for (WindowBuilder windowComponentBuilder : windowComponentBuilders) {
                Window window = windowComponentBuilder.create(parentRouter);
                parentRouter.addChild(window.getID(), window);
            }
            for (RouterBuilder routerBuilder : routerBuilders) {
                Router routerChild = routerBuilder.create(parentRouter);
                parentRouter.addRoute(routerChild.getID(), routerChild);
            }
        }

    }

}
