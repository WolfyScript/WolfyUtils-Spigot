package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.gui.ClickInteractionDetails;
import com.wolfyscript.utilities.common.gui.ClickType;
import com.wolfyscript.utilities.common.gui.Data;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickInteractionDetailsImpl<D extends Data> implements ClickInteractionDetails<D> {

    private InventoryClickEvent clickEvent;

    ClickInteractionDetailsImpl(InventoryClickEvent clickEvent) {
        this.clickEvent = clickEvent;
    }

    @Override
    public boolean isShift() {
        return clickEvent.isShiftClick();
    }

    @Override
    public boolean isSecondary() {
        return clickEvent.isRightClick();
    }

    @Override
    public boolean isPrimary() {
        return clickEvent.isLeftClick();
    }

    @Override
    public int getSlot() {
        return clickEvent.getSlot();
    }

    @Override
    public int getRawSlot() {
        return clickEvent.getRawSlot();
    }

    @Override
    public int getHotbarButton() {
        return clickEvent.getHotbarButton();
    }

    @Override
    public ClickType getClickType() {
        return switch (clickEvent.getClick()) {
            case DROP -> ClickType.DROP;
            case CONTROL_DROP -> ClickType.CONTROL_DROP;
            case LEFT -> ClickType.PRIMARY;
            case RIGHT -> ClickType.SECONDARY;
            case SHIFT_LEFT -> ClickType.SHIFT_PRIMARY;
            case SHIFT_RIGHT -> ClickType.SHIFT_SECONDARY;
            case MIDDLE -> ClickType.MIDDLE;
            case CREATIVE -> ClickType.CREATIVE;
            case NUMBER_KEY -> ClickType.NUMBER_KEY;
            case DOUBLE_CLICK -> ClickType.DOUBLE_CLICK;
            case WINDOW_BORDER_LEFT -> ClickType.CONTAINER_BORDER_PRIMARY;
            case WINDOW_BORDER_RIGHT -> ClickType.CONTAINER_BORDER_SECONDARY;
            default -> throw new IllegalStateException("Unexpected value: " + clickEvent.getClick());
        };
    }

    @Override
    public boolean isCancelled() {
        return clickEvent.isCancelled();
    }

    @Override
    public InteractionResult.ResultType getResultType() {
        return null;
    }
}
