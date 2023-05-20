package com.wolfyscript.utilities.bukkit.gui;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.Router;
import com.wolfyscript.utilities.common.gui.RouterBuilder;
import com.wolfyscript.utilities.common.gui.WindowBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class RouterBuilderImpl implements RouterBuilder {

    private final String id;
    private final WolfyUtils wolfyUtils;
    private final Map<String, RouterBuilder> subRouteBuilders = new HashMap<>();
    private WindowBuilder windowBuilder = null;
    private InteractionCallback interactionCallback = (guiHolder, componentState, interactionDetails) -> InteractionResult.def();

    @Inject
    @JsonCreator
    RouterBuilderImpl(@JsonProperty("id") String routerID,
                      @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils) {
        this.id = routerID;
        this.wolfyUtils = wolfyUtils;
    }

    private class RouteJsonContainer {

    }

    @JsonSetter("routes")
    private void readRoutes(ArrayNode arrayNode) {

    }

    @Override
    public RouterBuilder interact(InteractionCallback interactionCallback) {
        Preconditions.checkArgument(interactionCallback != null);
        this.interactionCallback = interactionCallback;
        return this;
    }

    @Override
    public RouterBuilder route(String s, Consumer<RouterBuilder> consumer) {
        consumer.accept(subRouteBuilders.computeIfAbsent(s, s1 -> new RouterBuilderImpl(s, wolfyUtils)));
        return this;
    }

    @Override
    public RouterBuilder window(Consumer<WindowBuilder> consumer) {
        this.windowBuilder = new WindowBuilderImpl("", wolfyUtils);
        consumer.accept(windowBuilder);
        return this;
    }

    public Router create(Router parent) {
        return new RouterImpl(
                id,
                wolfyUtils,
                windowBuilder,
                parent,
                interactionCallback
        );
    }

}
