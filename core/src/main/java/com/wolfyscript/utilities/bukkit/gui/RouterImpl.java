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
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.InteractionDetails;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.MenuComponent;
import com.wolfyscript.utilities.common.gui.Router;
import com.wolfyscript.utilities.common.gui.RouterBuilder;
import com.wolfyscript.utilities.common.gui.RouterChildBuilder;
import com.wolfyscript.utilities.common.gui.RouterEntry;
import com.wolfyscript.utilities.common.gui.RouterEntryBuilder;
import com.wolfyscript.utilities.common.gui.Window;
import com.wolfyscript.utilities.common.gui.WindowComponentBuilder;
import java.util.ArrayList;
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
    public Class<ComponentStateRouterImpl> getComponentStateType() {
        return ComponentStateRouterImpl.class;
    }

    @Override
    public void open(GuiViewManager viewManager, UUID player) {
        // Redirect and open the entry component
        entry().component().open(viewManager, player);
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

    public ComponentState createNewState(ComponentState componentState, int i) {
        return null;
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
        private InteractionCallback interactionCallback = (guiHolder, componentState, interactionDetails) -> InteractionResult.cancel(true);

        Builder(String routerID, Builder parent, WolfyUtils wolfyUtils) {
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

            ChildBuilder() { }

            public <CT extends Component.Builder<? extends Window, ?>> RouterChildBuilder custom(String subID, NamespacedKey builderId, Class<CT> builderType, Consumer<CT> builderConsumer) {
                // TODO
                return this;
            }

            @Override
            public RouterChildBuilder window(String id, Consumer<WindowComponentBuilder> windowComponentBuilderConsumer) {
                var windowBuilder = new WindowImpl.BuilderImpl(id, Builder.this);
                windowComponentBuilderConsumer.accept(windowBuilder);
                windowComponentBuilders.add(windowBuilder);
                return this;
            }

            @Override
            public RouterChildBuilder router(String id, Consumer<RouterBuilder> clusterComponentBuilderConsumer) {
                RouterBuilder clusterBuilder = new RouterImpl.Builder(id, Builder.this, wolfyUtils);
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
