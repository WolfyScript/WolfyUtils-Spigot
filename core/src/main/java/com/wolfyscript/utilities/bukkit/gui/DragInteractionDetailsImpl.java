package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.gui.DragInteractionDetails;
import com.wolfyscript.utilities.common.gui.DragType;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import java.util.Set;
import org.bukkit.event.inventory.InventoryDragEvent;

public class DragInteractionDetailsImpl implements DragInteractionDetails {

    private final InventoryDragEvent event;

    public DragInteractionDetailsImpl(InventoryDragEvent event) {
        this.event = event;
    }

    @Override
    public Set<Integer> getInventorySlots() {
        return event.getInventorySlots();
    }

    @Override
    public Set<Integer> getRawSlots() {
        return event.getRawSlots();
    }

    @Override
    public DragType getType() {
        return switch (event.getType()) {
            case EVEN -> DragType.EVEN;
            case SINGLE -> DragType.SINGLE;
        };
    }

    @Override
    public boolean isCancelled() {
        return event.isCancelled();
    }

    @Override
    public InteractionResult.ResultType getResultType() {
        return null;
    }
}
