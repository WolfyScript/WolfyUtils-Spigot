package com.wolfyscript.utilities.bukkit.gui;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import com.wolfyscript.utilities.common.gui.components.ButtonBuilder;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentBuilder;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.components.Router;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.components.Window;
import com.wolfyscript.utilities.common.gui.components.WindowBuilder;
import com.wolfyscript.utilities.common.gui.components.WindowChildComponentBuilder;
import com.wolfyscript.utilities.common.gui.components.WindowTitleUpdateCallback;
import com.wolfyscript.utilities.common.gui.WindowType;
import com.wolfyscript.utilities.common.registry.RegistryGUIComponentBuilders;
import com.wolfyscript.utilities.json.annotations.KeyedBaseType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@KeyedStaticId(key = "window")
@KeyedBaseType(baseType = ComponentBuilder.class)
public class WindowBuilderImpl  implements WindowBuilder {

    private final String id;
    private final WolfyUtils wolfyUtils;
    private ChildBuilderImpl childComponentBuilder;
    protected int size;
    protected WindowType type;
    protected WindowTitleUpdateCallback titleUpdateCallback = (guiHolder, window, state) -> net.kyori.adventure.text.Component.empty();
    private InteractionCallback interactionCallback = (guiHolder, componentState, interactionDetails) -> InteractionResult.def();
    private final Map<String, Signal.Builder<?>> signalBuilderMap = new HashMap<>();
    private final WindowRenderer.Builder rendererBuilder;

    @Inject
    @JsonCreator
    protected WindowBuilderImpl(@JsonProperty("id") String windowID,
                                @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils) {
        this.id = windowID;
        this.wolfyUtils = wolfyUtils;
        this.rendererBuilder = new WindowRenderer.Builder(null);
    }

    @JsonSetter("children")
    private void setChildren(ChildBuilderImpl childBuilder) {
        this.childComponentBuilder = childBuilder;
        this.childComponentBuilder.setParentBuilder(this);
    }

    private WindowChildComponentBuilder getChildBuilder() {
        if (childComponentBuilder == null) {
            childComponentBuilder = new ChildBuilderImpl(wolfyUtils);
            childComponentBuilder.setParentBuilder(this);
        }
        return childComponentBuilder;
    }

    @JsonSetter("signals")
    private void setSignals(List<SignalImpl.Builder<?>> builders) {
        for (Signal.Builder<?> builder : builders) {
            signalBuilderMap.putIfAbsent(builder.getKey(), builder);
        }
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

    @JsonSetter("inventory_type")
    @Override
    public WindowBuilder type(WindowType type) {
        this.type = type;
        return this;
    }

    @Override
    public WindowBuilder title(WindowTitleUpdateCallback titleUpdateCallback) {
        Preconditions.checkNotNull(titleUpdateCallback);
        this.titleUpdateCallback = titleUpdateCallback;
        return this;
    }

    @Override
    public <T> WindowBuilder createSignal(String key, Class<T> type, Consumer<Signal.Builder<T>> signalBuilder) {
        if (signalBuilderMap.containsKey(key)) {
            Signal.Builder<?> builder = signalBuilderMap.get(key);
            if (builder.getValueType() != type)
                throw new IllegalArgumentException("A builder for '" + key + "' of a different type already exists!");
            signalBuilder.accept((Signal.Builder<T>) builder);
            return this;
        }
        SignalImpl.Builder<T> builder = new SignalImpl.Builder<>(key, type);
        signalBuilder.accept(builder);
        this.signalBuilderMap.put(key, builder);
        return this;
    }

    public WindowBuilder children(Consumer<WindowChildComponentBuilder> childComponentBuilderConsumer) {
        childComponentBuilderConsumer.accept(getChildBuilder());
        return this;
    }

    @Override
    public WindowBuilder interact(InteractionCallback interactionCallback) {
        Preconditions.checkNotNull(interactionCallback);
        this.interactionCallback = interactionCallback;
        return this;
    }

    @Override
    public WindowBuilder render(Consumer<com.wolfyscript.utilities.common.gui.WindowRenderer.Builder> render) {
        Preconditions.checkNotNull(render);
        render.accept(rendererBuilder);
        return this;
    }

    @Override
    public Window create(Router parent) {
        return new WindowImpl(
                parent.getID() + "/" + id,
                parent,
                size,
                type,
                this.titleUpdateCallback,
                interactionCallback,
                getChildBuilder(),
                rendererBuilder
        );
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChildBuilderImpl implements WindowChildComponentBuilder {

        private final BiMap<String, ComponentBuilder<? extends Component, Component>> children = HashBiMap.create();
        private final WolfyUtils wolfyUtils;
        private WindowBuilder parentBuilder;

        @JsonCreator
        public ChildBuilderImpl(@JacksonInject("wolfyUtils") WolfyUtils wolfyUtils) {
            this.wolfyUtils = wolfyUtils;
        }

        @JsonIgnore
        protected void setParentBuilder(WindowBuilder parentBuilder) {
            this.parentBuilder = parentBuilder;
        }

        @JsonSetter("values")
        private void createChildBuilders(List<ComponentBuilder<? extends Component, Component>> children) {
            for (ComponentBuilder<? extends Component, Component> child : children) {
                this.children.put(child.getID(), child);
            }
        }

        @Override
        public <B extends ComponentBuilder<? extends Component, Component>> WindowChildComponentBuilder custom(String componentId, Class<B> builderType, Consumer<B> builderConsumer) {
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
                binder.bind(WindowBuilder.class).toInstance(parentBuilder);
                binder.bind(String.class).toInstance(componentId);
            });
            B builder = injector.getInstance(builderImplType);
            children.put(componentId, builder);
            builderConsumer.accept(builder);
            return this;
        }

        @Override
        public WindowChildComponentBuilder button(String buttonId, Consumer<ButtonBuilder> consumer) {
            return custom(buttonId, ButtonBuilder.class, consumer);
        }

        @Override
        public void applyTo(Window window) {
            if (!(window instanceof WindowImpl thisWindow)) return;
            children.forEach((s, componentBuilder) -> thisWindow.addNewChildComponent(s, componentBuilder.create(null)));
        }

    }

}
