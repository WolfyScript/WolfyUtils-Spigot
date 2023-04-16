package com.wolfyscript.utilities.bukkit.gui;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Button;
import com.wolfyscript.utilities.common.gui.ButtonBuilder;
import com.wolfyscript.utilities.common.gui.ButtonIcon;
import com.wolfyscript.utilities.common.gui.ComponentBuilderSettings;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.SizedComponent;
import com.wolfyscript.utilities.common.items.ItemStackConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@KeyedStaticId(key = "button")
@ComponentBuilderSettings(base = ButtonBuilder.class, component = Button.class)
public class ButtonBuilderImpl extends AbstractBukkitComponentBuilder<Button, SizedComponent> implements ButtonBuilder {

    private InteractionCallback interactionCallback = (guiHolder, componentState, interactionDetails) -> InteractionResult.cancel(true);
    private final IconBuilderImpl iconBuilder;
    private final Map<String, Signal<?>> signals;

    @JsonCreator
    @Inject
    public ButtonBuilderImpl(@JsonProperty("id") String id, @JsonProperty("icon") IconBuilderImpl iconBuilder, @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils) {
        super(id, wolfyUtils);
        this.iconBuilder = iconBuilder;
        this.signals = new HashMap<>();
    }

    @Override
    public ButtonBuilder icon(Consumer<IconBuilder> consumer) {
        consumer.accept(iconBuilder);
        return this;
    }

    @Override
    public ButtonBuilder interact(InteractionCallback interactionCallback) {
        Preconditions.checkArgument(interactionCallback != null, "InteractionCallback must be non-null!");
        this.interactionCallback = interactionCallback;
        return this;
    }

    @Override
    public <T> ButtonBuilder useSignal(String key, Class<T> type, Consumer<Signal.Builder<T>> signalBuilder) {
        SignalImpl.Builder<T> builder = new SignalImpl.Builder<>(key, type);
        signalBuilder.accept(builder);
        this.signals.put(key, builder.create());
        return this;
    }

    @Override
    public Button create(SizedComponent parent) {
        return new ButtonImpl(getWolfyUtils(), getID(), parent, iconBuilder.create(), interactionCallback, signals);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IconBuilderImpl implements IconBuilder {

        @JsonProperty("stack")
        private BukkitItemStackConfig stackConfig;
        @JsonProperty("dynamic")
        private boolean dynamic = false;

        @JsonSetter("stack")
        private void setStack(BukkitItemStackConfig config) {
            this.stackConfig = config;
        }

        @JsonSetter("dynamic")
        private void setDynamic(boolean dynamic) {
            this.dynamic = dynamic;
        }

        @Override
        public IconBuilder stack(ItemStackConfig<?> itemStackConfig) {
            if (itemStackConfig instanceof BukkitItemStackConfig bukkitItemStackConfig) {
                this.stackConfig = bukkitItemStackConfig;
            }
            return this;
        }

        @Override
        public IconBuilder stack(Supplier<ItemStackConfig<?>> supplier) {
            if (supplier.get() instanceof BukkitItemStackConfig bukkitItemStackConfig) {
                this.stackConfig = bukkitItemStackConfig;
            }
            return this;
        }

        @Override
        public IconBuilder dynamic() {
            return dynamic(true);
        }

        @Override
        public IconBuilder dynamic(boolean isDynamic) {
            this.dynamic = isDynamic;
            return this;
        }

        @Override
        public ButtonIcon create() {
            if (dynamic) {
                return new ButtonImpl.DynamicIcon(stackConfig);
            }
            return new ButtonImpl.StaticIcon(stackConfig);
        }
    }

}
