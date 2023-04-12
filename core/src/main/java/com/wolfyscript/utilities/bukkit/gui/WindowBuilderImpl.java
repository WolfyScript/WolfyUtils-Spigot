package com.wolfyscript.utilities.bukkit.gui;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.ButtonBuilder;
import com.wolfyscript.utilities.common.gui.ComponentBuilder;
import com.wolfyscript.utilities.common.gui.ComponentBuilderSettings;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.RenderCallback;
import com.wolfyscript.utilities.common.gui.Router;
import com.wolfyscript.utilities.common.gui.RouterBuilder;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.SizedComponent;
import com.wolfyscript.utilities.common.gui.Window;
import com.wolfyscript.utilities.common.gui.WindowBuilder;
import com.wolfyscript.utilities.common.gui.WindowChildComponentBuilder;
import com.wolfyscript.utilities.common.gui.WindowState;
import com.wolfyscript.utilities.common.gui.WindowTitleUpdateCallback;
import com.wolfyscript.utilities.common.gui.WindowType;
import com.wolfyscript.utilities.common.registry.RegistryGUIComponentBuilders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@KeyedStaticId(key = "window")
@ComponentBuilderSettings(base = WindowBuilder.class, component = Window.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WindowBuilderImpl extends AbstractBukkitComponentBuilder<Window, Router> implements WindowBuilder {

    protected final RouterBuilder parent;
    protected WindowChildComponentBuilder childComponentBuilder;
    protected Integer size;
    protected WindowType type;
    protected WindowTitleUpdateCallback titleUpdateCallback;
    private InteractionCallback interactionCallback = (guiHolder, componentState, interactionDetails) -> InteractionResult.def();
    private RenderCallback<WindowState> renderCallback = (guiHolder, componentState) -> {};
    private final Map<String, Signal<?>> signals;

    @Inject
    @JsonCreator
    protected WindowBuilderImpl(@JsonProperty("id") String windowID, @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils, @JacksonInject("parent") RouterBuilderImpl parent) {
        super(windowID, wolfyUtils);
        this.parent = parent;
        this.signals = new HashMap<>();
    }

    @JsonSetter("children")
    private void setChildren(ChildBuilderImpl childBuilder) {
        this.childComponentBuilder = childBuilder;
    }

    @JsonSetter("size")
    @Override
    public WindowBuilder size(int size) {
        this.size = size;
        return this;
    }

    @JsonSetter("type")
    @Override
    public WindowBuilder type(WindowType type) {
        this.type = type;
        return this;
    }

    @Override
    public WindowBuilder title(WindowTitleUpdateCallback titleUpdateCallback) {
        this.titleUpdateCallback = titleUpdateCallback;
        return this;
    }

    @Override
    public <T> WindowBuilder useSignal(String key, Class<T> type, Consumer<Signal.Builder<T>> signalBuilder) {
        SignalImpl.Builder<T> builder = new SignalImpl.Builder<>(key, type);
        signalBuilder.accept(builder);
        this.signals.put(key, builder.create());
        return this;
    }

    public WindowBuilder children(Consumer<WindowChildComponentBuilder> childComponentBuilderConsumer) {
        if (childComponentBuilder == null) {
            childComponentBuilder = new ChildBuilderImpl();
        }
        childComponentBuilderConsumer.accept(childComponentBuilder);
        return this;
    }

    @Override
    public Window create(Router parent) {
        Window window = new WindowImpl(parent.getID() + "/" + getID(), parent, this.titleUpdateCallback, size, type, interactionCallback, renderCallback, signals);
        childComponentBuilder.applyTo(window);
        return window;
    }

    @Override
    public WindowBuilder interact(InteractionCallback interactionCallback) {
        Preconditions.checkNotNull(interactionCallback);
        this.interactionCallback = interactionCallback;
        return this;
    }

    @Override
    public WindowBuilder render(RenderCallback<WindowState> renderCallback) {
        Preconditions.checkNotNull(renderCallback);
        this.renderCallback = renderCallback;
        return this;
    }

    public class ChildBuilderImpl implements WindowChildComponentBuilder {

        private final BiMap<String, ComponentBuilder<? extends SizedComponent, SizedComponent>> children = HashBiMap.create();

        @JsonCreator
        ChildBuilderImpl() {
            super();
        }

        @JsonSetter("values")
        private void createChildBuilders(List<ComponentBuilder<? extends SizedComponent, SizedComponent>> children) {
            for (ComponentBuilder<? extends SizedComponent, SizedComponent> child : children) {
                this.children.put(child.getID(), child);
            }
        }

        @Override
        public <B extends ComponentBuilder<? extends SizedComponent, SizedComponent>> WindowChildComponentBuilder custom(String componentId, Class<B> builderType, Consumer<B> builderConsumer) {
            RegistryGUIComponentBuilders registry = getWolfyUtils().getRegistries().getGuiComponentBuilders();

            NamespacedKey key = registry.getKey(builderType);
            Preconditions.checkArgument(key != null, "Failed to create component '%s'! Cannot find builder '%s' in registry!", componentId, builderType.getName());

            @SuppressWarnings("unchecked")
            Class<B> builderImplType = (Class<B>) registry.get(key); // We can be sure that the cast is valid, because the key is only non-null if and only if the type matches!
            if (builderImplType != null) {
                Injector injector = Guice.createInjector(Stage.PRODUCTION, binder -> {
                    binder.bind(WolfyUtils.class).toInstance(getWolfyUtils());
                    binder.bind(ComponentBuilder.class).toInstance(WindowBuilderImpl.this);
                    binder.bind(String.class).toInstance(componentId);
                });
                B builder = injector.getInstance(builderImplType);
                children.put(componentId, builder);
                builderConsumer.accept(builder);
            }
            return this;
        }

        @Override
        public WindowChildComponentBuilder button(String buttonId, Consumer<ButtonBuilder> consumer) {
            if (children.containsKey(buttonId)) {
                if (!(children.get(buttonId) instanceof ButtonBuilderImpl builder))
                    throw new IllegalArgumentException("A builder for '" + buttonId + "' of a different type already exists!");
                consumer.accept(builder);
                return this;
            }
            ButtonBuilderImpl builder = new ButtonBuilderImpl(buttonId, getWolfyUtils());
            children.putIfAbsent(buttonId, builder);
            consumer.accept(builder);
            return this;
        }

        @Override
        public void applyTo(Window window) {
            if (!(window instanceof WindowImpl thisWindow)) return;

            children.forEach((s, componentBuilder) -> {
                thisWindow.addNewChildComponent(s, componentBuilder.create(thisWindow));
            });

        }
    }

}
