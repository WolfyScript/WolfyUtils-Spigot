package com.wolfyscript.utilities.bukkit.gui.components;

import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.gui.AbstractBukkitComponent;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.components.Button;
import com.wolfyscript.utilities.common.gui.components.ButtonComponentState;
import com.wolfyscript.utilities.common.gui.components.ButtonIcon;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.InteractionDetails;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.MenuComponent;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.SizedComponent;
import com.wolfyscript.utilities.common.gui.components.Window;
import com.wolfyscript.utilities.common.items.ItemStackConfig;
import com.wolfyscript.utilities.eval.context.EvalContext;
import java.util.Map;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.ItemStack;

@KeyedStaticId(key = "button")
public class ButtonImpl extends AbstractBukkitComponent implements Button {

    private final Map<String, Signal<?>> signals;
    private final InteractionCallback interactionCallback;
    private final ButtonIcon icon;

    public ButtonImpl(WolfyUtils wolfyUtils, String id, SizedComponent parent, ButtonIcon icon, InteractionCallback interactionCallback, Map<String, Signal<?>> signals) {
        super(id, wolfyUtils, parent);
        this.icon = icon;
        this.interactionCallback = interactionCallback;
        this.signals = Map.copyOf(signals);
    }

    public SizedComponent parent() {
        return (SizedComponent) super.parent();
    }

    @Override
    public void init() {

    }

    @Override
    public ButtonIcon icon() {
        return icon;
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
    public ButtonComponentState createState(ComponentState state, GuiHolder holder) {
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

}
