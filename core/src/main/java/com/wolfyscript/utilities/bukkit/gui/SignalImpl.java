package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.Signal;
import java.util.function.Function;

public class SignalImpl<MT> implements Signal<MT> {

    private final String key;
    private final Class<MT> messageValueType;
    private final Function<ComponentState, MT> defaultValueFunction;

    public SignalImpl(String key, Class<MT> messageValueType, Function<ComponentState, MT> defaultValueFunction) {
        this.key = key;
        this.messageValueType = messageValueType;
        this.defaultValueFunction = defaultValueFunction;
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public Class<MT> messageType() {
        return messageValueType;
    }

    @Override
    public Value<MT> createMessage(ComponentState componentState) {
        MT value = defaultValueFunction.apply(componentState);
        return new ValueImpl<>((ComponentStateImpl<?, ?>) componentState, this, value);
    }

    public static class ValueImpl<T> implements Value<T> {

        private final ComponentStateImpl<?, ?> state;
        private final Signal<T> signal;
        private final T value;

        public ValueImpl(ComponentStateImpl<?, ?> state, Signal<T> signal, T value) {
            this.state = state;
            this.signal = signal;
            this.value = value;
        }

        @Override
        public Signal<T> signal() {
            return signal;
        }

        @Override
        public ComponentState state() {
            return state;
        }

        @Override
        public void update(T newValue) {
            state.updateMessage(new ValueImpl<>(state, signal, newValue));
        }

        @Override
        public void update(Function<T, T> function) {
            update(function.apply(value));
        }

        @Override
        public T get() {
            return value;
        }
    }

    public static class Builder<T> implements Signal.Builder<T> {

        private final String key;
        private final Class<T> messageValueType;
        private Function<ComponentState, T> defaultValueFunction = state -> null;

        public Builder(String key, Class<T> messageValueType) {
            this.key = key;
            this.messageValueType = messageValueType;
        }

        @Override
        public Builder<T> defaultValue(Function<ComponentState, T> defaultValueFunction) {
            Preconditions.checkNotNull(defaultValueFunction, "Default value function cannot be set to null!");
            this.defaultValueFunction = defaultValueFunction;
            return this;
        }

        @Override
        public Signal<T> create() {
            return new SignalImpl<>(key, messageValueType, defaultValueFunction);
        }

    }

}
