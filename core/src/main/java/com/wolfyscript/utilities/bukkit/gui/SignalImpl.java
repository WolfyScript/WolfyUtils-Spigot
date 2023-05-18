package com.wolfyscript.utilities.bukkit.gui;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.Signalable;
import java.util.Objects;
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
    public Class<MT> valueType() {
        return messageValueType;
    }

    @Override
    public Value<MT> createValue(ComponentState componentState) {
        MT value = defaultValueFunction.apply(componentState);
        return new ValueImpl<>((ComponentStateImpl<?, ?>) componentState, this, value);
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

    public static class ValueImpl<T> implements Value<T> {

        private final ComponentStateImpl<?, ?> state;
        private final Signal<T> signal;
        private T value;

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
        public void set(T newValue) {
            this.value = newValue;
            if (state instanceof Signalable signalable) {
                signalable.receiveUpdate(this);
            }
        }

        @Override
        public void update(Function<T, T> function) {
            set(function.apply(value));
        }

        @Override
        public T get() {
            return value;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Builder<T> implements Signal.Builder<T> {

        private final String key;
        private final Class<T> messageValueType;
        private Function<ComponentState, T> defaultValueFunction = state -> null;

        @JsonCreator
        public Builder(@JsonProperty("key") String key, @JsonProperty("type") Class<T> messageValueType) {
            this.key = key;
            this.messageValueType = messageValueType;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Class<T> getValueType() {
            return messageValueType;
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
