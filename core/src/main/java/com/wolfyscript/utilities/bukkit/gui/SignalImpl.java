package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.Signalable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class SignalImpl<MT> implements Signal<MT> {

    private final String key;
    private final Class<MT> messageValueType;
    private MT value;
    private final Map<ComponentState, MT> values = new HashMap<>();
    private ComponentState currentState = null;

    public SignalImpl(String key, Class<MT> messageValueType, Supplier<MT> defaultValueFunction) {
        this.key = key;
        this.messageValueType = messageValueType;
        this.value = defaultValueFunction.get();
    }

    public void enter(ComponentState currentState) {
        this.currentState = currentState;
    }

    public void exit() {
        this.currentState = null;
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public Class<MT> valueType() {
        return messageValueType;
    }

    @Override
    public void set(MT newValue) {
        this.value = newValue;
        if (currentState != null) {
            if (currentState instanceof Signalable signalable) {
                signalable.receiveUpdate(this);
            }
            values.put(currentState, newValue);
        }
    }

    @Override
    public void update(Function<MT, MT> function) {
        set(function.apply(value));
    }

    @Override
    public MT get() {
        return values.computeIfAbsent(currentState, state -> value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignalImpl<?> signal = (SignalImpl<?>) o;
        return Objects.equals(key, signal.key) && Objects.equals(messageValueType, signal.messageValueType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, messageValueType);
    }

}
