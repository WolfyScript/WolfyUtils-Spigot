package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.collect.BiMap;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.InteractionDetails;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.MenuComponent;
import com.wolfyscript.utilities.common.gui.RenderCallback;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.Router;
import com.wolfyscript.utilities.common.gui.RouterCommonImpl;
import com.wolfyscript.utilities.common.gui.RouterComponentBuilder;
import com.wolfyscript.utilities.common.gui.WindowComponentBuilder;

public class ClusterImpl extends RouterCommonImpl {

    protected ClusterImpl(String id, WolfyUtils wolfyUtils, Router parent, BiMap<String, ? extends MenuComponent> children, MenuComponent entry) {
        super(id, wolfyUtils, parent, children, entry);
    }

    @Override
    public InteractionResult interact(GuiHolder holder, ComponentState state, InteractionDetails interactionDetails) {
        return null;
    }

    @Override
    public void render(GuiHolder holder, ComponentState state, RenderContext context) {

    }

    @Override
    public InteractionCallback interactCallback() {
        return null;
    }

    @Override
    public RenderCallback renderCallback() {
        return null;
    }

    public static class Builder extends RouterCommonImpl.Builder {

        protected Builder(String subID, Router parent) {
            super(subID, parent, new ChildBuilder(parent));
        }

        @Override
        protected Router constructImplementation(String subID, WolfyUtils wolfyUtils, Router router,  BiMap<String, ? extends MenuComponent> children, MenuComponent entry) {
            return new ClusterImpl(subID, wolfyUtils, router, children, entry);
        }

    }

    public static class ChildBuilder extends RouterCommonImpl.ChildBuilder {

        protected ChildBuilder(Router parent) {
            super(parent);
        }

        @Override
        protected RouterComponentBuilder constructClusterBuilderImpl(String id, Router router) {
            return new ClusterImpl.Builder(id, router);
        }

        @Override
        protected WindowComponentBuilder constructWindowBuilderImpl(String id, Router router) {
            return new WindowImpl.BuilderImpl(id, router);
        }
    }

}
