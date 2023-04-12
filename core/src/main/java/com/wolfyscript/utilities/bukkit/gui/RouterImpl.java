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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Inject;
import com.wolfyscript.utilities.KeyedStaticId;
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
import com.wolfyscript.utilities.common.gui.RouterEntry;
import com.wolfyscript.utilities.common.gui.RouterState;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.Window;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@KeyedStaticId(key = "router")
public final class RouterImpl extends AbstractBukkitComponent implements Router {

    private final Map<String, Signal<?>> signals;
    private final BiMap<String, Window> children = HashBiMap.create();
    private final BiMap<String, Router> routes = HashBiMap.create();
    private RouterEntry entry;
    private final InteractionCallback interactionCallback;

    @Inject
    @JsonCreator
    RouterImpl(String id, WolfyUtils wolfyUtils, Router parent, Map<String, Signal<?>> signals, InteractionCallback interactionCallback) {
        super(id, wolfyUtils, parent);
        Preconditions.checkNotNull(interactionCallback);
        this.interactionCallback = interactionCallback;
        this.signals = Map.copyOf(signals);
    }

    @Override
    public Router parent() {
        return (Router) super.parent();
    }

    public void init() {

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
    public RouterEntry entry() {
        return entry;
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
    public Map<String, Signal<?>> signals() {
        return signals;
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
        return InteractionResult.cancel(false);
    }

    @Override
    public InteractionCallback interactCallback() {
        return interactionCallback;
    }

}
