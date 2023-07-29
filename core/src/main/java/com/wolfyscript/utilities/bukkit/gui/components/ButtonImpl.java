package com.wolfyscript.utilities.bukkit.gui.components;

import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.eval.context.EvalContextPlayer;
import com.wolfyscript.utilities.bukkit.gui.GUIHolder;
import com.wolfyscript.utilities.bukkit.gui.GuiViewManagerImpl;
import com.wolfyscript.utilities.bukkit.gui.RenderContextImpl;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.*;
import com.wolfyscript.utilities.common.gui.callback.InteractionCallback;
import com.wolfyscript.utilities.common.gui.components.Button;
import com.wolfyscript.utilities.common.gui.components.ButtonIcon;
import com.wolfyscript.utilities.common.gui.impl.AbstractComponentImpl;
import com.wolfyscript.utilities.common.items.ItemStackConfig;
import com.wolfyscript.utilities.eval.context.EvalContext;
import it.unimi.dsi.fastutil.ints.IntList;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.ItemStack;

@KeyedStaticId(key = "button")
public class ButtonImpl extends AbstractComponentImpl implements Button {

    private final InteractionCallback interactionCallback;
    private final ButtonIcon icon;

    public ButtonImpl(WolfyUtils wolfyUtils, String id, Component parent, ButtonIcon icon, InteractionCallback interactionCallback, IntList slots) {
        super(id, wolfyUtils, parent, slots);
        this.icon = icon;
        this.interactionCallback = interactionCallback;
    }

    public ButtonImpl(ButtonImpl button) {
        super(button.getID(), button.getWolfyUtils(), button.parent(), button.getSlots());
        this.interactionCallback = button.interactionCallback;
        this.icon = button.icon;
    }

    @Override
    public ButtonIcon icon() {
        return icon;
    }

    @Override
    public InteractionResult interact(GuiHolder guiHolder, InteractionDetails interactionDetails) {
        if (parent() instanceof Interactable interactableParent) {
            InteractionResult result = interactableParent.interact(guiHolder, interactionDetails);
            if (result.isCancelled()) return result;
        }
        return interactionCallback.interact(guiHolder, interactionDetails);
    }

    @Override
    public InteractionCallback interactCallback() {
        return interactionCallback;
    }

    @Override
    public Button construct(GuiHolder holder, GuiViewManager guiViewManager) {
        return this;
    }

    @Override
    public void remove(GuiHolder guiHolder, GuiViewManager guiViewManager, RenderContext renderContext) {
        for (int slot : getSlots()) {
            renderContext.setNativeStack(slot, null);
            ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(null, slot);
        }
    }

    @Override
    public void update(GuiViewManager viewManager, GuiHolder guiHolder, RenderContext context) {
        if (!(context instanceof RenderContextImpl renderContext)) return;
        renderContext.setNativeStack(renderContext.getCurrentOffset(),
                ((BukkitItemStackConfig) icon().getStack()).constructItemStack(
                        new EvalContextPlayer(((GUIHolder) guiHolder).getBukkitPlayer()),
                        WolfyCoreBukkit.getInstance().getWolfyUtils().getChat().getMiniMessage(),
                        icon().getResolvers()
                )
        );
    }

    public static class DynamicIcon implements ButtonIcon {

        private final BukkitItemStackConfig config;
        private final TagResolver resolvers;

        DynamicIcon(BukkitItemStackConfig config, TagResolver resolvers) {
            this.config = config;
            this.resolvers = resolvers;
        }

        @Override
        public ItemStackConfig<?> getStack() {
            return config;
        }

        public ItemStack create(MiniMessage miniMessage, EvalContext evalContext, TagResolver... tagResolvers) {
            return config.constructItemStack(evalContext, miniMessage, tagResolvers);
        }

        public TagResolver getResolvers() {
            return resolvers;
        }

    }

}
