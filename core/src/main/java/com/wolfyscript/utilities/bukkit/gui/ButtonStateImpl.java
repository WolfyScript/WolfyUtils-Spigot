package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.RenderContext;

public class ButtonStateImpl extends ComponentStateImpl<ButtonImpl, ComponentState> {

    public ButtonStateImpl(ComponentState parent, ButtonImpl button) {
        super(parent, button);
    }

    @Override
    public void render(GuiHolder holder, RenderContext context) {
        context.setStack(0, getOwner().getIcon());
    }
}
