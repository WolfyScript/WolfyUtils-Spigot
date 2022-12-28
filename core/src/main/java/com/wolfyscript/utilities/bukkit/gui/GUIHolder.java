package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.bukkit.gui.button.Button;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonItemInput;
import com.wolfyscript.utilities.bukkit.gui.button.ButtonType;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import com.wolfyscript.utilities.common.gui.ButtonInteractionResult;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class GUIHolder<C extends CustomCache> implements InventoryHolder {

    private final GuiWindow<C> window;
    private Inventory activeInventory;
    private final GuiHandler<C> guiHandler;
    private final Player player;

    public GUIHolder(Player player, GuiHandler<C> guiHandler, GuiWindow<C> window) {
        this.guiHandler = guiHandler;
        this.window = window;
        this.player = player;
    }

    public GuiHandler<C> getGuiHandler() {
        return guiHandler;
    }

    public GuiWindow<C> getWindow() {
        return window;
    }

    public Player getPlayer() {
        return player;
    }

    void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (window == null) return;
        Map<Integer, Button<C>> buttons = new HashMap<>();
        if (activeInventory.equals(event.getClickedInventory())) {
            Button<C> clickedBtn = guiHandler.getButton(window, event.getSlot());
            if (clickedBtn != null) {
                buttons.put(event.getSlot(), clickedBtn);
                event.setCancelled(executeButton(clickedBtn, event.getSlot()).isCancelled());
                if (Objects.equals(clickedBtn.getType(), ButtonType.ITEM_SLOT)) { //If the button is marked as an Item slot it may affect other buttons too!
                    if (event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR) || event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                        var clickedBtnClass = clickedBtn.getClass();
                        for (Map.Entry<Integer, String> buttonEntry : guiHandler.getCustomCache().getButtons(window).entrySet()) {
                            if (event.getSlot() != buttonEntry.getKey()) {
                                Button<C> button = window.getButton(buttonEntry.getValue());
                                if (clickedBtnClass.isInstance(button)) { //Make sure to only execute the buttons that are of the same type as the clicked one.
                                    buttons.put(buttonEntry.getKey(), button);
                                    event.setCancelled(executeButton(button, buttonEntry.getKey()).isCancelled());
                                }
                            }
                        }
                    }
                }
            }
        } else if (!event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
            event.setCancelled(false);
            if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                for (Map.Entry<Integer, String> buttonEntry : guiHandler.getCustomCache().getButtons(window).entrySet()) {
                    Button<C> button = window.getButton(buttonEntry.getValue());
                    if (button instanceof ButtonItemInput) {
                        buttons.put(buttonEntry.getKey(), button);
                        if (executeButton(button, buttonEntry.getKey()).isCancelled()) {
                            event.setCancelled(true);
                            break;
                        }
                    }
                }
            }
        }
        if (guiHandler.openedPreviousWindow) {
            guiHandler.openedPreviousWindow = false;
        } else if (guiHandler.getWindow() != null && guiHandler.isWindowOpen()) {
            window.update(activeInventory, guiHandler, buttons);
        }
    }

    void onDrag(InventoryDragEvent event) {
        if (event.getRawSlots().parallelStream().anyMatch(rawSlot -> !Objects.equals(event.getView().getInventory(rawSlot), activeInventory))) {
            event.setCancelled(true);
            return;
        }
        if (window != null) {
            Map<Integer, Button<C>> buttons = new HashMap<>();
            for (int slot : event.getInventorySlots()) {
                Button<C> button = guiHandler.getButton(window, slot);
                if (button == null) {
                    event.setCancelled(true);
                    return;
                }
                buttons.put(slot, button);
            }
            for (Map.Entry<Integer, Button<C>> button : buttons.entrySet()) {
                event.setCancelled(executeButton(button.getValue(), button.getKey()).isCancelled());
            }
            if (guiHandler.openedPreviousWindow) {
                guiHandler.openedPreviousWindow = false;
            } else if (guiHandler.getWindow() != null) {
                window.update(activeInventory, guiHandler, buttons);
            }
        }
    }

    void onClose(InventoryCloseEvent event) {
        guiHandler.onClose(this);
    }

    void setActiveInventory(Inventory activeInventory) {
        this.activeInventory = activeInventory;
    }

    private ButtonInteractionResult executeButton(Button<C> button, int slot) {
        try {
            return button.execute(this, slot);
        } catch (IOException e) {
            e.printStackTrace();
            return ButtonInteractionResult.def();
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return activeInventory;
    }
}
