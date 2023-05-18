package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.NativeRendererModule;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.Renderer;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;

public class ComponentRenderer implements Renderer<ComponentState> {

    private final ComponentStateImpl<?,?> state;

    private final Renderer parent;
    final Multimap<String, Integer> componentPositions = ArrayListMultimap.create();
    final Multimap<String, Renderer> reactiveParts = ArrayListMultimap.create();

    private final Map<Integer, ComponentStateImpl<? extends Component, ComponentState>> updatedStateCache = new Int2ObjectOpenHashMap<>();

    public ComponentRenderer(ComponentStateImpl<?, ?> state, Renderer parent) {
        this.state = state;
        this.parent = parent;
    }

    @Override
    public int getWidth() {
        return state.getOwner().width();
    }

    @Override
    public int getHeight() {
        return state.getOwner().height();
    }

    @Override
    public void render(ComponentState state, GuiHolder guiHolder, RenderContext renderContext) {

    }

    @Override
    public void renderComponent(ComponentState state, int i, Component component) {

    }

    @Override
    public NativeRendererModule getNativeModule() {
        return null;
    }
}
