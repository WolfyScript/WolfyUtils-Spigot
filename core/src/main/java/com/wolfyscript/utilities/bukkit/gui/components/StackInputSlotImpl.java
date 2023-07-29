package com.wolfyscript.utilities.bukkit.gui.components;

import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl;
import com.wolfyscript.utilities.bukkit.gui.ClickInteractionDetailsImpl;
import com.wolfyscript.utilities.bukkit.gui.GuiViewManagerImpl;
import com.wolfyscript.utilities.bukkit.gui.InteractionUtils;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.adapters.ItemStack;
import com.wolfyscript.utilities.common.gui.*;
import com.wolfyscript.utilities.common.gui.callback.InteractionCallback;
import com.wolfyscript.utilities.common.gui.components.StackInputSlot;
import com.wolfyscript.utilities.common.gui.impl.AbstractComponentImpl;
import com.wolfyscript.utilities.common.gui.signal.Signal;
import it.unimi.dsi.fastutil.ints.IntList;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Set;
import java.util.function.Consumer;

@KeyedStaticId(key = "stack_input_slot")
public class StackInputSlotImpl extends AbstractComponentImpl implements Interactable, StackInputSlot {

    private final Consumer<ItemStack> onValueChange;
    private final InteractionCallback interactionCallback;
    private final Signal<ItemStack> value;

    public StackInputSlotImpl(String internalID, WolfyUtils wolfyUtils, Component parent, Consumer<ItemStack> onValueChange, InteractionCallback interactionCallback, Signal<ItemStack> value, IntList slots) {
        super(internalID, wolfyUtils, parent, slots);
        this.onValueChange = onValueChange;
        this.interactionCallback = (holder, details) -> {
            if (details instanceof ClickInteractionDetailsImpl clickInteractionDetails) {
                InventoryClickEvent event = clickInteractionDetails.getClickEvent();
                InteractionUtils.applyItemFromInteractionEvent(event.getSlot(), event, Set.of(), itemStack -> {
                    onValueChange.accept(new ItemStackImpl((WolfyUtilsBukkit) holder.getViewManager().getWolfyUtils(), itemStack));
                });
            }
            return interactionCallback.interact(holder, details);
        };
        this.value = value;
        value.linkTo(this);
    }

    @Override
    public StackInputSlot construct(GuiHolder holder, GuiViewManager guiViewManager) {
        return this;
    }

    @Override
    public void update(GuiViewManager viewManager, GuiHolder guiHolder, RenderContext renderContext) {
        if (!(value.get() instanceof ItemStackImpl stackImpl)) return;
        for (int slot : getSlots()) {
            renderContext.setNativeStack(slot, stackImpl.getBukkitRef());
            ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(this, slot);
        }
    }

    @Override
    public void remove(GuiHolder guiHolder, GuiViewManager guiViewManager, RenderContext renderContext) {
        for (int slot : getSlots()) {
            renderContext.setNativeStack(slot, null);
            ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(null, slot);
        }
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
    public InteractionResult interact(GuiHolder guiHolder, InteractionDetails interactionDetails) {
        return interactionCallback.interact(guiHolder, interactionDetails);
    }

    @Override
    public InteractionCallback interactCallback() {
        return interactionCallback;
    }

    @Override
    public Signal<ItemStack> signal() {
        return value;
    }
}
