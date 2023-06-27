package com.wolfyscript.utilities.bukkit.gui.components;

import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl;
import com.wolfyscript.utilities.bukkit.gui.AbstractBukkitComponent;
import com.wolfyscript.utilities.bukkit.gui.ClickInteractionDetailsImpl;
import com.wolfyscript.utilities.bukkit.gui.ComponentStateImpl;
import com.wolfyscript.utilities.bukkit.gui.InteractionUtils;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.adapters.ItemStack;
import com.wolfyscript.utilities.common.gui.*;
import com.wolfyscript.utilities.common.gui.components.StackInputSlot;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Set;
import java.util.function.Consumer;

@KeyedStaticId(key = "stack_input_slot")
public class StackInputSlotImpl extends AbstractBukkitComponent implements Interactable, StackInputSlot {

    private final Consumer<ItemStack> onValueChange;
    private final InteractionCallback interactionCallback;
    private final Signal<ItemStack> value;

    public StackInputSlotImpl(String internalID, WolfyUtils wolfyUtils, Component parent, Consumer<ItemStack> onValueChange, InteractionCallback interactionCallback, Signal<ItemStack> value) {
        super(internalID, wolfyUtils, parent);
        this.onValueChange = onValueChange;
        this.interactionCallback = (holder, state, details) -> {
            if (details instanceof ClickInteractionDetailsImpl clickInteractionDetails) {
                InventoryClickEvent event = clickInteractionDetails.getClickEvent();
                InteractionUtils.applyItemFromInteractionEvent(event.getSlot(), event, Set.of(), itemStack -> {
                    onValueChange.accept(new ItemStackImpl((WolfyUtilsBukkit) holder.getViewManager().getWolfyUtils(), itemStack));
                });
            }
            return interactionCallback.interact(holder, state, details);
        };
        this.value = value;
    }

    @Override
    public Renderer<? extends ComponentState> getRenderer() {
        return new StackInputSlotRenderer(this);
    }

    @Override
    public int width() {
        return 1;
    }

    @Override
    public int height() {
        return 1;
    }

    public Signal<ItemStack> getValue() {
        return value;
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
    public ComponentState createState(ComponentState componentState, GuiViewManager guiViewManager) {
        return new ComponentStateImpl<>(componentState, this) {



        };
    }

    @Override
    public Signal<ItemStack> signal() {
        return value;
    }
}
