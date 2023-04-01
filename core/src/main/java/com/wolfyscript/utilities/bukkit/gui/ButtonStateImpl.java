package com.wolfyscript.utilities.bukkit.gui;

import com.google.inject.Inject;
import com.wolfyscript.utilities.bukkit.eval.context.EvalContextPlayer;
import com.wolfyscript.utilities.common.gui.Button;
import com.wolfyscript.utilities.common.gui.ButtonComponentState;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.RenderContext;
import org.bukkit.inventory.ItemStack;

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
            renderContext.setNativeStack(renderContext.getSlotOffsetToParent(), (ItemStack) getOwner().icon().getStack().constructItemStack(new EvalContextPlayer(((GUIHolder) holder).getPlayer())));
        }
    }

}
