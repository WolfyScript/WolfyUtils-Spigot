package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.InteractionDetails;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.RenderContext;

public abstract class ComponentImpl implements Component {

    @Override
    public InteractionResult interact(GuiHolder holder, ComponentState state, InteractionDetails interactionDetails) {
        if (parent() != null) {
            InteractionResult result = parent().interact(holder, ((ComponentStateImpl) state).getParent(), interactionDetails);
            if (result.isCancelled()) return result;
        }
        return interactCallback().interact(holder, state, interactionDetails);
    }

    @Override
    public void render(GuiHolder holder, ComponentState state, RenderContext context) {
        renderCallback().render(holder, state, context);

        // TODO: Place Components/Items into the Inventory

    }

    @Override
    public ComponentState createNewState(ComponentState parentState, int relativePos) {
        return new ComponentStateImpl((ComponentStateImpl) parentState, this, relativePos);
    }

}
