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

    private final Map<String, Signal.Value<?>> signalValues = new HashMap<>();
    private final Deque<Signal.Value<?>> signalUpdateQueue = new ArrayDeque<>();

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

    @Override
    public void updateComponent(int i, ComponentState componentState) {
        childComponentStates.put(i, (ComponentStateImpl<? extends Component, ComponentState>) componentState);
    }

    @Override
    public Window getOwner() {
        return owner;
    }

    @Override
    public Map<String, Signal.Value<?>> getSignalValues() {
        return signalValues;
    }

    @Override
    public Deque<Signal.Value<?>> updatedSignals() {
        return signalUpdateQueue;
    }

    @Override
    public void receiveUpdate(Signal.Value<?> signal) {
        signalValues.putIfAbsent(signal.signal().key(), signal);
        signalUpdateQueue.push(signal);
    }
}
