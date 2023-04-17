package com.wolfyscript.utilities.bukkit.gui;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.ComponentBuilder;
import com.wolfyscript.utilities.common.gui.ComponentBuilderSettings;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.MenuComponent;
import com.wolfyscript.utilities.common.gui.Router;
import com.wolfyscript.utilities.common.gui.RouterBuilder;
import com.wolfyscript.utilities.common.gui.RouterChildBuilder;
import com.wolfyscript.utilities.common.gui.RouterEntry;
import com.wolfyscript.utilities.common.gui.RouterEntryBuilder;
import com.wolfyscript.utilities.common.gui.RouterState;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.WindowBuilder;
import com.wolfyscript.utilities.common.registry.RegistryGUIComponentBuilders;
import com.wolfyscript.utilities.json.annotations.KeyedBaseType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@KeyedStaticId(key = "router")
@ComponentBuilderSettings(base = RouterBuilder.class, component = Router.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@KeyedBaseType(baseType = ComponentBuilder.class)
public final class RouterBuilderImpl extends AbstractBukkitComponentBuilder<Router, Router> implements RouterBuilder {

    private ChildBuilder childComponentBuilder;
    private final RouterEntryBuilderImpl routerEntryBuilder = new RouterEntryBuilderImpl();
    private InteractionCallback interactionCallback = (guiHolder, componentState, interactionDetails) -> InteractionResult.def();
    private final Map<String, Signal.Builder<?>> signals;

    @Inject
    @JsonCreator
    RouterBuilderImpl(@JsonProperty("id") String routerID,
                      @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils) {
        super(routerID, wolfyUtils);
        this.signals = new HashMap<>();
    }

    @JsonSetter("children")
    private void setChildren(RouterBuilderImpl.ChildBuilder childBuilder) {
        this.childComponentBuilder = childBuilder;
        childComponentBuilder.setParentBuilder(this);
    }

    @JsonSetter("signals")
    private void setSignals(List<Signal.Builder<?>> signalBuilders) {
        for (Signal.Builder<?> signalBuilder : signalBuilders) {
            signals.put(signalBuilder.getKey(), signalBuilder);
        }
    }

    private ChildBuilder getChildComponentBuilder() {
        if (childComponentBuilder == null) setChildren(new ChildBuilder(getWolfyUtils()));
        return childComponentBuilder;
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
        childComponentBuilderConsumer.accept(this.getChildComponentBuilder());
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
        getChildComponentBuilder().applyTo(router);
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
                case WINDOW -> router.getChild(id).map(window -> new RouterEntryImpl(window, type))
                        .orElseThrow(() -> new IllegalStateException("Cannot find specified Window Entry '" + id + "' of Router: " + router.getID()));
                case ROUTER -> router.getRoute(id).map(router1 -> new RouterEntryImpl(router1, type))
                        .orElseThrow(() -> new IllegalStateException("Cannot find specified Router Entry '" + id + "' of Router: " + router.getID()));
            };
        }


    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static final class ChildBuilder implements RouterChildBuilder {

        private final Map<String, ComponentBuilder<? extends MenuComponent<?>, Router>> children = new HashMap<>();
        private final WolfyUtils wolfyUtils;
        private RouterBuilderImpl parentBuilder;

        @JsonCreator
        public ChildBuilder(@JacksonInject("wolfyUtils") WolfyUtils wolfyUtils) {
            this.wolfyUtils = wolfyUtils;
        }

        private void setParentBuilder(RouterBuilderImpl parentBuilder) {
            this.parentBuilder = parentBuilder;
        }

        @JsonSetter("menus")
        private void setMenus(List<ComponentBuilder<? extends MenuComponent<RouterState>, Router>> children) {
            for (ComponentBuilder<? extends MenuComponent<RouterState>, Router> child : children) {
                if (child instanceof WindowBuilder windowBuilder) {
                    this.children.putIfAbsent(windowBuilder.getID(), windowBuilder);
                }
            }
        }

        @JsonSetter("routes")
        private void setRoutes(List<ComponentBuilder<? extends MenuComponent<RouterState>, Router>> children) {
            for (ComponentBuilder<? extends MenuComponent<RouterState>, Router> child : children) {
                if (child instanceof RouterBuilder routerBuilder) {
                    this.children.putIfAbsent(routerBuilder.getID(), routerBuilder);
                }
            }
        }

        public <B extends ComponentBuilder<? extends MenuComponent<?>, Router>> RouterChildBuilder custom(String componentId, Class<B> builderType, Consumer<B> builderConsumer) {
            RegistryGUIComponentBuilders registry = wolfyUtils.getRegistries().getGuiComponentBuilders();
            NamespacedKey key = registry.getKey(builderType);
            Preconditions.checkArgument(key != null, "Failed to create component '%s'! Cannot find builder '%s' in registry!", componentId, builderType.getName());
            @SuppressWarnings("unchecked")
            Class<B> builderImplType = (Class<B>) registry.get(key); // We can be sure that the cast is valid, because the key is only non-null if and only if the type matches!
            Preconditions.checkNotNull(builderImplType, "Failed to create component '%s'! Cannot find implementation type of builder '%s' in registry!", componentId, builderType.getName());
            if (children.containsKey(componentId)) {
                ComponentBuilder<?, ?> componentBuilder = children.get(componentId);
                Preconditions.checkState(key.equals(componentBuilder.getType()), "A builder for '" + componentId + "' of a different type already exists!");
                builderConsumer.accept(builderImplType.cast(componentBuilder));
                return this;
            }
            Injector injector = Guice.createInjector(Stage.PRODUCTION, binder -> {
                binder.bind(WolfyUtils.class).toInstance(wolfyUtils);
                binder.bind(new TypeLiteral<ComponentBuilder<?,?>>(){}).annotatedWith(Names.named("parent")).toInstance(parentBuilder);
                binder.bind(String.class).toInstance(componentId);
            });
            B builder = injector.getInstance(builderImplType);
            children.put(componentId, builder);
            builderConsumer.accept(builder);
            return this;
        }

        @Override
        public RouterChildBuilder window(String id, Consumer<WindowBuilder> windowBuilderConsumer) {
            return custom(id, WindowBuilder.class, windowBuilderConsumer);
        }

        @Override
        public RouterChildBuilder router(String id, Consumer<RouterBuilder> routerBuilderConsumer) {
            return custom(id, RouterBuilder.class, routerBuilderConsumer);
        }

        @Override
        public void applyTo(Router router) {
            if (!(router instanceof RouterImpl parentRouter)) return;
            for (Map.Entry<String, ComponentBuilder<? extends MenuComponent<?>, Router>> entry : children.entrySet()) {
                ComponentBuilder<? extends MenuComponent<?>, Router> builder = entry.getValue();
                if (builder instanceof WindowBuilder windowBuilder) {
                    parentRouter.addChild(windowBuilder.getID(), windowBuilder.create(router));
                } else if (builder instanceof RouterBuilder routerBuilder) {
                    parentRouter.addRoute(routerBuilder.getID(), routerBuilder.create(router));
                }
            }
        }

    }

}
