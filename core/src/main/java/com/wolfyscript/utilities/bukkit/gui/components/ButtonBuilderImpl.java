package com.wolfyscript.utilities.bukkit.gui.components;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.gui.AbstractBukkitComponentBuilder;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.components.Button;
import com.wolfyscript.utilities.common.gui.components.ButtonBuilder;
import com.wolfyscript.utilities.common.gui.components.ButtonIcon;
import com.wolfyscript.utilities.common.gui.ComponentBuilderSettings;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.items.ItemStackConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

@KeyedStaticId(key = "button")
@ComponentBuilderSettings(base = ButtonBuilder.class, component = Button.class)
public class ButtonBuilderImpl extends AbstractBukkitComponentBuilder<Button, Component> implements ButtonBuilder {

    private InteractionCallback interactionCallback = (guiHolder, componentState, interactionDetails) -> InteractionResult.cancel(true);
    private final IconBuilderImpl iconBuilder;

    /**
     * Constructor used for non-config setups using Guice injection.
     *
     * @param id The id of the button.
     * @param wolfyUtils The wolfyutils that this button belongs to.
     */
    @Inject
    private ButtonBuilderImpl(String id, WolfyUtils wolfyUtils, List<Integer> slots) {
        super(id, wolfyUtils, slots);
        this.iconBuilder = new IconBuilderImpl();
    }

    @JsonCreator
    public ButtonBuilderImpl(@JsonProperty("id") String id, @JsonProperty("icon") IconBuilderImpl iconBuilder, @JacksonInject("wolfyUtils") WolfyUtils wolfyUtils, @JsonProperty("slots") List<Integer> slots) {
        super(id, wolfyUtils, slots);
        this.iconBuilder = iconBuilder;
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
    public Button create(Component parent) {
        return new ButtonImpl(getWolfyUtils(), getID(), parent, iconBuilder.create(), interactionCallback);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IconBuilderImpl implements IconBuilder {

        @JsonProperty("stack")
        private BukkitItemStackConfig stackConfig;
        private final List<TagResolver> tagResolvers = new ArrayList<>();

        @Inject
        private IconBuilderImpl() {
            // Used for non-config setups
        }

        /**
         * Constructor for reading the icon builder from config.
         *
         * @param stackConfig The necessary stack config.
         */
        @JsonCreator
        public IconBuilderImpl(@JsonProperty("stack") BukkitItemStackConfig stackConfig) {
            this.stackConfig = stackConfig;
        }

        @JsonSetter("stack")
        private void setStack(BukkitItemStackConfig config) {
            this.stackConfig = config;
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
        public IconBuilder updateOnSignals(Signal<?>... signals) {
            this.tagResolvers.addAll(Arrays.stream(signals)
                    .map(signal -> TagResolver.resolver(signal.key(), (argumentQueue, context) -> Tag.inserting(net.kyori.adventure.text.Component.text(String.valueOf(signal.get())))))
                    .toList());
            return this;
        }

        public IconBuilder addTagResolver(TagResolver... tagResolvers) {
            this.tagResolvers.add(TagResolver.resolver(tagResolvers));
            return this;
        }

        @Override
        public ButtonIcon create() {
            return new ButtonImpl.DynamicIcon(stackConfig, TagResolver.resolver(tagResolvers));
        }
    }

}
