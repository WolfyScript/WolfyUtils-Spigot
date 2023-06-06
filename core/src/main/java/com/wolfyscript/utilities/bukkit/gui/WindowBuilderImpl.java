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
import com.wolfyscript.utilities.common.gui.ComponentBuilder;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.Router;
import com.wolfyscript.utilities.common.gui.Window;
import com.wolfyscript.utilities.common.gui.WindowBuilder;
import com.wolfyscript.utilities.common.gui.WindowTitleUpdateCallback;
import com.wolfyscript.utilities.common.gui.WindowType;
import com.wolfyscript.utilities.json.annotations.KeyedBaseType;
import java.util.function.Consumer;

@KeyedStaticId(key = "window")
@KeyedBaseType(baseType = ComponentBuilder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WindowBuilderImpl  implements WindowBuilder {

    private final String id;
    private final WolfyUtils wolfyUtils;
    protected int size;
    protected WindowType type;
    protected WindowTitleUpdateCallback titleUpdateCallback = (guiHolder, window, state) -> net.kyori.adventure.text.Component.empty();
    private InteractionCallback interactionCallback = (guiHolder, componentState, interactionDetails) -> InteractionResult.def();
    private WindowRenderer.Builder rendererBuilder;

    @Inject
    @JsonCreator
    protected WindowBuilderImpl(@JsonProperty("id") String windowID,
                                @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils) {
        this.id = windowID;
        this.wolfyUtils = wolfyUtils;
    }

    @JsonSetter("render")
    private void setRenderSettings(WindowRenderer.Builder builder) {
        this.rendererBuilder = builder;
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
    public WindowBuilder interact(InteractionCallback interactionCallback) {
        Preconditions.checkNotNull(interactionCallback);
        this.interactionCallback = interactionCallback;
        return this;
    }

    @Override
    public WindowBuilder render(Consumer<com.wolfyscript.utilities.common.gui.WindowRenderer.Builder> render) {
        Preconditions.checkNotNull(render);
        if (rendererBuilder == null) {
            this.rendererBuilder = new WindowRenderer.Builder(wolfyUtils);
        }
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
                rendererBuilder
        );
    }

}
