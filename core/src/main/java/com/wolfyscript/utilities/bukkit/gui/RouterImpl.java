/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.InteractionDetails;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.MenuComponent;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.Router;
import com.wolfyscript.utilities.common.gui.RouterBuilder;
import com.wolfyscript.utilities.common.gui.RouterChildBuilder;
import com.wolfyscript.utilities.common.gui.RouterEntry;
import com.wolfyscript.utilities.common.gui.RouterEntryBuilder;
import com.wolfyscript.utilities.common.gui.RouterState;
import com.wolfyscript.utilities.common.gui.Window;
import com.wolfyscript.utilities.common.gui.WindowComponentBuilder;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public final class RouterImpl implements Router {

    private final WolfyUtils wolfyUtils;
    private final Router parent;
    private final String id;
    private final BiMap<String, Window> children = HashBiMap.create();
    private final BiMap<String, Router> routes = HashBiMap.create();
    private RouterEntry entry;
    private final InteractionCallback interactionCallback;

    RouterImpl(String id, WolfyUtils wolfyUtils, Router parent, InteractionCallback interactionCallback) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(wolfyUtils);
        Preconditions.checkNotNull(interactionCallback);
        this.parent = parent;
        this.wolfyUtils = wolfyUtils;
        this.id = id;
        this.interactionCallback = interactionCallback;
    }

    void addChild(String id, Window child) {
        Preconditions.checkArgument(id != null);
        Preconditions.checkArgument(child != null);
        this.children.put(id, child);
    }

    void addRoute(String id, Router route) {
        Preconditions.checkArgument(id != null);
        Preconditions.checkArgument(route != null);
        this.routes.put(id, route);
    }

    void setEntry(RouterEntry entry) {
        Preconditions.checkNotNull(entry);
        Preconditions.checkNotNull(entry.id());
        Preconditions.checkNotNull(entry.type());
        this.entry = entry;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public Router parent() {
        return parent;
    }

    @Override
    public RouterEntry entry() {
        return entry;
    }

    public void init() {

    }

    @Override
    public RenderContext createContext(GuiViewManager guiViewManager, Deque<String> path, UUID uuid) {
        RenderContextImpl context;
        if (path.isEmpty()) {
            // construct context for entry because path reached the end
            context = (RenderContextImpl) entry().component().createContext(guiViewManager, path, uuid);
        } else {
            String childId = path.pop();
            context = (RenderContextImpl) getChild(childId).map(window -> window.createContext(guiViewManager, path, uuid))
                    .or(() -> getRoute(childId).map(router -> router.createContext(guiViewManager, path, uuid)))
                    .orElseThrow(() -> new IllegalArgumentException("Invalid path for MenuComponent! Cannot find child component/route '" + childId + "' of parent '" + getID() + "'!"));
        }
        context.pushParentOnPath(this);
        return context;
    }

    @Override
    public RouterState createState(ComponentState state) {
        if (state == null) {
            return new RouterStateImpl(null, this);
        }
        if (!(state instanceof RouterState parentState))
            throw new IllegalArgumentException("Cannot create router state without a router parent!");
        return new RouterStateImpl(parentState, this);
    }

    @Override
    public void open(GuiViewManager viewManager, RouterState parentState, Deque<String> path, UUID player) {
        RouterState currentState = createState(parentState);
        String id = path.poll();
        getChild(id)
                .ifPresentOrElse(window -> window.open(viewManager, currentState, path, player), () ->
                        getRoute(id)
                                .ifPresentOrElse(childRouter -> childRouter.open(viewManager, currentState, path, player), () -> {
                                    // open entry
                                    MenuComponent<RouterState> childComponent = entry.component();
                                    childComponent.open(viewManager, currentState, path, player);
                                })
                );
    }

    @Override
    public Optional<Window> getChild(String id) {
        return Optional.ofNullable(children.get(id));
    }

    @Override
    public Optional<Window> getChild(String... strings) {
        return Optional.empty();
    }

    @Override
    public Optional<Router> getRoute(String routeID) {
        return Optional.ofNullable(routes.get(routeID));
    }

    @Override
    public Set<? extends Window> childComponents() {
        return children.values();
    }

    @Override
    public Set<? extends Router> childRoutes() {
        return routes.values();
    }

    @Override
    public InteractionResult interact(GuiHolder guiHolder, ComponentState componentState, InteractionDetails interactionDetails) {
        return null;
    }

    @Override
    public InteractionCallback interactCallback() {
        return interactionCallback;
    }

    public static final class Builder implements RouterBuilder {

        private final String routerID;
        private final Builder parent;
        final WolfyUtils wolfyUtils;
        private final ChildBuilder childComponentBuilder;
        private final RouterEntryBuilderImpl routerEntryBuilder = new RouterEntryBuilderImpl();
        private InteractionCallback interactionCallback = (guiHolder, componentState, interactionDetails) -> InteractionResult.def();

        Builder(WolfyUtils wolfyUtils, String routerID, Builder parent) {
            Preconditions.checkNotNull(routerID);
            this.wolfyUtils = wolfyUtils;
            this.parent = parent;
            this.routerID = routerID;
            this.childComponentBuilder = new ChildBuilder();
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
            RouterImpl router = new RouterImpl(routerID, wolfyUtils, parent, interactionCallback);
            childComponentBuilder.applyTo(router);
            Preconditions.checkState(!router.children.isEmpty() || router.routes.isEmpty(), "Cannot create Router without child Components and Routes!");
            router.setEntry(routerEntryBuilder.build(router));
            return router;
        }

        public static final class RouterEntryBuilderImpl implements RouterEntryBuilder {

            private String id;
            private RouterEntry.Type type;

            RouterEntryBuilderImpl() {
                this.id = null;
                this.type = null;
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
                    MenuComponent component = router.children.values()
                            .stream()
                            .map(MenuComponent.class::cast)
                            .findFirst()
                            .or(() -> router.routes.values()
                                    .stream()
                                    .map(MenuComponent.class::cast)
                                    .findFirst()
                            ).orElseThrow(() -> new IllegalStateException("Cannot automatically determine an Entry of Router: " + router.id));
                    return new RouterEntryImpl(component, component instanceof Window ? RouterEntry.Type.WINDOW : RouterEntry.Type.ROUTER);
                }
                final MenuComponent component = switch (type) {
                    case WINDOW ->
                            router.getChild(id).orElseThrow(() -> new IllegalStateException("Cannot find specified Window Entry '" + id + "' of Router: " + router.id));
                    case ROUTER ->
                            router.getRoute(id).orElseThrow(() -> new IllegalStateException("Cannot find specified Router Entry '" + id + "' of Router: " + router.id));
                };
                return new RouterEntryImpl(component, type);
            }

        }

        final class ChildBuilder implements RouterChildBuilder {

            private final List<WindowComponentBuilder> windowComponentBuilders = new ArrayList<>();
            private final List<RouterBuilder> routerBuilders = new ArrayList<>();

            ChildBuilder() {
            }

            @Override
            public RouterChildBuilder window(String id, Consumer<WindowComponentBuilder> windowComponentBuilderConsumer) {
                var windowBuilder = new WindowImpl.BuilderImpl(wolfyUtils, id, Builder.this);
                windowComponentBuilderConsumer.accept(windowBuilder);
                windowComponentBuilders.add(windowBuilder);
                return this;
            }

            @Override
            public RouterChildBuilder router(String id, Consumer<RouterBuilder> clusterComponentBuilderConsumer) {
                RouterBuilder clusterBuilder = new RouterImpl.Builder(wolfyUtils, id, Builder.this);
                clusterComponentBuilderConsumer.accept(clusterBuilder);
                routerBuilders.add(clusterBuilder);
                return this;
            }

            @Override
            public void applyTo(Router router) {
                if (!(router instanceof RouterImpl parentRouter)) return;
                for (WindowComponentBuilder windowComponentBuilder : windowComponentBuilders) {
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
}
