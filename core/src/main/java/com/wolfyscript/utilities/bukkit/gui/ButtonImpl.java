package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Button;
import com.wolfyscript.utilities.common.gui.ButtonBuilder;
import com.wolfyscript.utilities.common.gui.ButtonComponentState;
import com.wolfyscript.utilities.common.gui.ButtonIcon;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.InteractionDetails;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.MenuComponent;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.SizedComponent;
import com.wolfyscript.utilities.common.gui.Window;
import com.wolfyscript.utilities.common.items.ItemStackConfig;
import com.wolfyscript.utilities.eval.context.EvalContext;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.ItemStack;

public class ButtonImpl implements Button {

    private final Map<String, Signal<?>> signals;
    private final WolfyUtils wolfyUtils;
    private final String id;
    private final SizedComponent parent;
    private final InteractionCallback interactionCallback;
    private final ButtonIcon icon;

    public ButtonImpl(WolfyUtils wolfyUtils, String id, SizedComponent parent, ButtonIcon icon, InteractionCallback interactionCallback, Map<String, Signal<?>> signals) {
        this.wolfyUtils = wolfyUtils;
        this.id = id;
        this.parent = parent;
        this.icon = icon;
        this.interactionCallback = interactionCallback;
        this.signals = Map.copyOf(signals);
    }

    @Override
    public ButtonIcon icon() {
        return icon;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public SizedComponent parent() {
        return parent;
    }

    @Override
    public void init() {

    }

    @Override
    public Map<String, Signal<?>> signals() {
        return signals;
    }

    @Override
    public InteractionResult interact(GuiHolder guiHolder, ComponentState componentState, InteractionDetails interactionDetails) {
        return interactionCallback.interact(guiHolder, componentState, interactionDetails);
    }

    @Override
    public InteractionCallback interactCallback() {
        return interactionCallback;
    }

    @Override
    public ButtonComponentState createState(ComponentState state) {
        Component parent = state.getOwner();
        if (parent instanceof MenuComponent<?> && !(parent instanceof Window))
            throw new IllegalArgumentException("Cannot create window state without a router parent!");
        return new ButtonStateImpl(state, this);
    }

    public static class StaticIcon implements ButtonIcon {

        private final BukkitItemStackConfig config;
        private final ItemStack stack;

        StaticIcon(BukkitItemStackConfig config) {
            this.config = config;
            this.stack = config.constructItemStack();
        }

        public ItemStack getStaticStack() {
            return stack;
        }

        @Override
        public ItemStackConfig<?> getStack() {
            return config;
        }

        public ItemStack create(MiniMessage miniMessage, EvalContext evalContext, TagResolver... tagResolvers) {
            return stack;
        }

        @Override
        public boolean isDynamic() {
            return false;
        }
    }

    public static class DynamicIcon implements ButtonIcon {

        private final BukkitItemStackConfig config;

        DynamicIcon(BukkitItemStackConfig config) {
            this.config = config;
        }

        @Override
        public ItemStackConfig<?> getStack() {
            return config;
        }

        public ItemStack create(MiniMessage miniMessage, EvalContext evalContext, TagResolver... tagResolvers) {
            return config.constructItemStack(evalContext, miniMessage, tagResolvers);
        }

        @Override
        public boolean isDynamic() {
            return true;
        }
    }

    public static class Builder implements ButtonBuilder, ComponentBuilder<Button, SizedComponent> {

        private final String id;
        private final WolfyUtils wolfyUtils;
        private InteractionCallback interactionCallback;
        private final IconBuilderImpl iconBuilder;
        private final Map<String, Signal<?>> signals;

        public Builder(String id, WolfyUtils wolfyUtils) {
            this.wolfyUtils = wolfyUtils;
            this.id = id;
            this.iconBuilder = new IconBuilderImpl();
            this.signals = new HashMap<>();
        }

        @Override
        public ButtonBuilder icon(Consumer<IconBuilder> consumer) {
            consumer.accept(iconBuilder);
            return this;
        }

        @Override
        public ButtonBuilder interact(InteractionCallback interactionCallback) {
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
            return new ButtonImpl(wolfyUtils, id, parent, iconBuilder.create(), interactionCallback, signals);
        }

        public static class IconBuilderImpl implements IconBuilder {

            private BukkitItemStackConfig stackConfig;
            private boolean dynamic = false;

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
                    return new DynamicIcon(stackConfig);
                }
                return new StaticIcon(stackConfig);
            }
        }

    }

}