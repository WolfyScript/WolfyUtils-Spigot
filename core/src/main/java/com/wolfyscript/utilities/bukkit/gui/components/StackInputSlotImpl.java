package com.wolfyscript.utilities.bukkit.gui.components;

import com.wolfyscript.utilities.bukkit.gui.AbstractBukkitComponent;
import com.wolfyscript.utilities.bukkit.gui.ClickInteractionDetailsImpl;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.adapters.ItemStack;
import com.wolfyscript.utilities.common.gui.ClickInteractionDetails;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.Interactable;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.InteractionDetails;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.Renderer;
import java.util.function.Function;

public class StackInputSlotImpl extends AbstractBukkitComponent implements Interactable {

    private Function<ItemStack, Boolean> onValueChangeFunction;

    public StackInputSlotImpl(String internalID, WolfyUtils wolfyUtils, Component parent) {
        super(internalID, wolfyUtils, parent);
    }

    @Override
    public Renderer<? extends ComponentState> getRenderer() {
        return null;
    }

    @Override
    public int width() {
        return 1;
    }

    @Override
    public int height() {
        return 1;
    }

    @Override
    public InteractionResult interact(GuiHolder guiHolder, ComponentState componentState, InteractionDetails interactionDetails) {
        if (interactionDetails instanceof ClickInteractionDetailsImpl clickInteractionDetails) {
            clickInteractionDetails.getClickEvent();

        }
        return null;
    }

    @Override
    public InteractionCallback interactCallback() {
        return null;
    }
}
