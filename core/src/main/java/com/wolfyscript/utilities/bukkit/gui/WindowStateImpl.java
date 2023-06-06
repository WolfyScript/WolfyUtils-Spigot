package com.wolfyscript.utilities.bukkit.gui;

import com.google.inject.Inject;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.Window;
import com.wolfyscript.utilities.common.gui.WindowState;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WindowStateImpl implements WindowState {

    final Map<Integer, ComponentStateImpl<? extends Component, ComponentState>> childComponentStates = new Int2ObjectOpenHashMap<>();
    final Map<Integer, Map<Integer, ComponentStateImpl<?,?>>> reactiveBoundStates = new Int2ObjectOpenHashMap<>();

    private final Map<String, Signal<?>> signalValues = new HashMap<>();
    private final Deque<Signal<?>> signalUpdateQueue = new ArrayDeque<>();

    private final Window owner;
    final GuiViewManager viewManager;

    @Inject
    public WindowStateImpl(Window owner, GuiViewManager viewManager) {
        this.owner = owner;
        this.viewManager = viewManager;
    }

    @Override
    public Optional<ComponentState> get(int i) {
        return Optional.ofNullable(childComponentStates.get(i));
    }

    public void updateReactiveComponent(int functionId, int slot, ComponentState componentState) {
        reactiveBoundStates.compute(functionId, (integer, componentStates) -> {
            if (componentStates == null) {
                componentStates = new Int2ObjectOpenHashMap<>();
            }
            componentStates.put(slot, (ComponentStateImpl<? extends Component, ComponentState>) componentState);
            return componentStates;
        });
    }

    @Override
    public void updateComponent(int i, ComponentState componentState) {
        childComponentStates.put(i, (ComponentStateImpl<? extends Component, ComponentState>) componentState);
    }

    @Override
    public Window getOwner() {
        return owner;
    }

    @Override
    public Map<String, Signal<?>> getSignalValues() {
        return signalValues;
    }

    @Override
    public Deque<Signal<?>> updatedSignals() {
        return signalUpdateQueue;
    }

    @Override
    public void receiveUpdate(Signal<?> signal) {
        signalValues.putIfAbsent(signal.key(), signal);
        signalUpdateQueue.push(signal);
    }
}
