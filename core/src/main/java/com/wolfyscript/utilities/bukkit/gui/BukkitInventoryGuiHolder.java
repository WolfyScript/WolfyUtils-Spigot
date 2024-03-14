package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.gui.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BukkitInventoryGuiHolder implements InventoryHolder {

    private final GuiHolder guiHolder;
    private Inventory activeInventory;

    public BukkitInventoryGuiHolder(GuiHolder guiHolder) {
        this.guiHolder = guiHolder;
    }

    private Window currentWindow() {
        return guiHolder.getCurrentWindow();
    }

    public GuiHolder guiHolder() {
        return guiHolder;
    }

    void onClick(InventoryClickEvent event) {
        if (currentWindow() == null || event.getClickedInventory() == null) return;
        if (Objects.equals(event.getClickedInventory().getHolder(), this)) {
            ViewRuntimeImpl guiViewManager = (ViewRuntimeImpl) guiHolder.getViewManager();
            // TODO: Call Interaction handler here
        } else if (!event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
            event.setCancelled(false);
            // TODO: Handle bottom inventory clicks
        }
        Bukkit.getScheduler().runTask(((WolfyCoreBukkit) guiHolder.getViewManager().getWolfyUtils().getCore()).getPlugin(),
                () -> {
//                    guiHolder.getViewManager().unblockedByInteraction();
                    guiHolder.getViewManager().getCurrentMenu().ifPresent(window -> {
                        window.open(guiHolder().getViewManager());
                    });
                }
        );
    }

    void onDrag(InventoryDragEvent event) {
        if (event.getRawSlots().stream().anyMatch(rawSlot -> !Objects.equals(event.getView().getInventory(rawSlot), activeInventory))) {
            event.setCancelled(true);
            return;
        }
        if (currentWindow() == null) return;
        if (Objects.equals(event.getInventory().getHolder(), this)) {
            var interactionDetails = new DragInteractionDetailsImpl(event);
            // TODO: Call Interaction handler here

            Bukkit.getScheduler().runTask(((WolfyCoreBukkit) guiHolder.getViewManager().getWolfyUtils().getCore()).getPlugin(), () -> {});
        }
    }

    void onClose(InventoryCloseEvent event) {
        // TODO: Close Window
        if (currentWindow() == null) return;
        if (Objects.equals(event.getInventory().getHolder(), this)) {
            guiHolder.getViewManager().getCurrentMenu().ifPresent(window -> window.close(guiHolder.getViewManager()));
        }
    }

    public void setActiveInventory(Inventory activeInventory) {
        this.activeInventory = activeInventory;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return activeInventory;
    }
}
