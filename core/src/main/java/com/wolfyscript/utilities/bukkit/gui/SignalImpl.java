package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.Signal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class SignalImpl<MT> implements Signal<MT> {

    private final String key;
    private final Class<MT> messageValueType;
    private MT value;
    private final Map<GuiViewManager, MT> values = new HashMap<>();
    private GuiViewManager activeViewManager = null;
    private boolean wasUpdated = false;

    public SignalImpl(String key, Class<MT> messageValueType, Supplier<MT> defaultValueFunction) {
        this.key = key;
        this.messageValueType = messageValueType;
        this.value = defaultValueFunction.get();
    }

    public void enter(GuiViewManager viewManager) {
        this.wasUpdated = false;
        this.activeViewManager = viewManager;
    }

    public boolean exit() {
        this.activeViewManager = null;

        boolean wasUpdated = this.wasUpdated;
        this.wasUpdated = false;
        return wasUpdated;
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
        if (activeViewManager == null) {
            this.value = newValue;
            return;
        }
        values.put(activeViewManager, newValue);
        this.wasUpdated = true;
    }

    @Override
    public void update(Function<MT, MT> function) {
        set(function.apply(values.getOrDefault(activeViewManager, value)));
    }

    @Override
    public MT get() {
        if (activeViewManager == null) {
            return value;
        }
        return values.computeIfAbsent(activeViewManager, state -> value);
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
