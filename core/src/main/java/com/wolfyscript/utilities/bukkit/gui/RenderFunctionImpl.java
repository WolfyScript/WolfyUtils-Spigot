package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.components.RenderFunction;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RenderFunctionImpl implements RenderFunction {

    private final ComponentStateImpl<?,?> state;
    private final RenderFunction parent;
    final Map<String, Integer> componentPositions = new HashMap<>();
    final List<String> staticParts = new ArrayList<>();
    final Object2ObjectOpenHashMap<String, RenderFunction> reactiveParts = new Object2ObjectOpenHashMap<>();

    public RenderFunctionImpl(ComponentStateImpl<?,?> state) {
        this(state, null);
    }

    public RenderFunctionImpl(ComponentStateImpl<?,?> state, RenderFunction parent) {
        this.state = state;
        this.parent = parent;
    }

    @Override
    public <T> Signal.Value<T> useSignal(String s, Class<T> aClass, Supplier<T> supplier) {
        return null;
    }

    @Override
    public RenderFunction position(int slot, String componentID) {
        this.componentPositions.put(componentID, slot);
        return this;
    }

    @Override
    public RenderFunction renderAt(int slot, String componentID) {

        return this;
    }

    @Override
    public RenderFunction render(String componentID) {
        // directly render component?
        return this;
    }

    @Override
    public <S> RenderFunction reactive(Signal.Value<S> value, BiConsumer<RenderFunction, Signal.Value<S>> consumer) {
        String key = value.signal().key();
        RenderFunction reactive = new RenderFunctionImpl(state);
        consumer.accept(reactive, value);
        reactiveParts.put(key, reactive);
        return this;
    }

    @Override
    public void run(GuiHolder guiHolder, ComponentState componentState) {

    }

}
