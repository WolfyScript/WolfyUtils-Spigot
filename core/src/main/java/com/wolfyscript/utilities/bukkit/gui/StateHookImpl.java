package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.gui.StateHook;
import java.util.Objects;
import java.util.function.Function;

public class StateHookImpl<V> implements StateHook<V> {

    private final ComponentStateImpl owner;
    private V value;

    StateHookImpl(ComponentStateImpl owner, V defaultValue) {
        this.value = defaultValue;
        this.owner = owner;
    }

    @Override
    public V get() {
        return value;
    }

    @Override
    public void set(V newValue) {
        if (!Objects.equals(newValue, value)) {
            value = newValue;
            owner.markChildDirty(owner);
        }
    }

    @Override
    public void set(Function<V, V> update) {
        set(update.apply(get()));
    }
}
