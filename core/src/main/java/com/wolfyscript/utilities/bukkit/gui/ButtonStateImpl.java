package com.wolfyscript.utilities.bukkit.gui;

import com.google.inject.Inject;
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.eval.context.EvalContextPlayer;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.gui.Button;
import com.wolfyscript.utilities.common.gui.ButtonComponentState;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.RenderContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class ButtonStateImpl extends ComponentStateImpl<Button, ComponentState> implements ButtonComponentState {

    @Inject
    public ButtonStateImpl(ComponentState parent, Button button) {
        super(parent, button);
    }

    @Override
    public void render(GuiHolder holder, RenderContext context) {
        if (!(context instanceof RenderContextImpl renderContext)) return;
        if (getOwner().icon() instanceof ButtonImpl.StaticIcon staticIcon) {
            renderContext.setNativeStack(renderContext.getSlotOffsetToParent(), staticIcon.getStaticStack());
        } else {
            renderContext.setNativeStack(renderContext.getSlotOffsetToParent(),
                    ((BukkitItemStackConfig) getOwner().icon().getStack()).constructItemStack(
                            new EvalContextPlayer(((GUIHolder) holder).getPlayer()),
                            WolfyCoreBukkit.getInstance().getWolfyUtils().getChat().getMiniMessage(),
                            TagResolver.resolver("signal", (argumentQueue, context1) -> {
                                String key = argumentQueue.popOr("Missing signal id!").value();
                                int lvlUp = 0;
                                if (argumentQueue.hasNext()) {
                                    lvlUp = argumentQueue.pop().asInt().orElse(0);
                                }
                                ComponentState state = this;
                                for (int i = 0; i < lvlUp; i++) {
                                    if (state.getParent() == null) break;
                                    state = state.getParent();
                                }
                                Object value = state.captureSignal(key).get();
                                return Tag.inserting(value == null ? Component.text("null") : context1.deserialize(value.toString()));
                            })
                    )
            );
        }
    }

}
