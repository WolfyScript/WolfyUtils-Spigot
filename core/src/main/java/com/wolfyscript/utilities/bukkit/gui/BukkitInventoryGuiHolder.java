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
    private final Player player;
    private Inventory activeInventory;

    public BukkitInventoryGuiHolder(Player player, GuiHolder guiHolder) {
        this.player = player;
        this.guiHolder = guiHolder;
    }

    public Player player() {
        return player;
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
            GuiViewManagerImpl guiViewManager = (GuiViewManagerImpl) guiHolder.getViewManager();
            guiHolder.getViewManager().getCurrentMenu().ifPresent(window -> {
                InteractionResult result = guiViewManager.getLeaveNode(event.getSlot())
                        .map(component -> {
                            if (component instanceof Interactable interactable) {
                                return interactable.interact(guiHolder, new ClickInteractionDetailsImpl(event));
                            }
                            return InteractionResult.cancel(true);
                        })
                        .orElse(InteractionResult.cancel(true));
                event.setCancelled(result.isCancelled());
            });
        } else if (!event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
            event.setCancelled(false);
            // TODO: Handle bottom inventory clicks
        }
        Bukkit.getScheduler().runTask(((WolfyCoreBukkit) guiHolder.getViewManager().getWolfyUtils().getCore()).getPlugin(),
                () -> {
                    guiHolder.getViewManager().unblockedByInteraction();
                    guiHolder.getViewManager().getRenderContext(event.getWhoClicked().getUniqueId()).ifPresent(context -> {
                        context.openAndRenderMenuFor(guiHolder.getViewManager(), player.getUniqueId());
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
            for (int slot : event.getInventorySlots()) {
                if (((GuiViewManagerImpl) guiHolder.getViewManager()).getLeaveNode(slot).map(component -> {
                    if (component instanceof Interactable interactable) {
                        return interactable.interact(guiHolder, interactionDetails);
                    }
                    return InteractionResult.cancel(true);
                }).orElse(InteractionResult.cancel(true)).isCancelled()) {
                    event.setCancelled(true);
                }
            }

            Bukkit.getScheduler().runTask(((WolfyCoreBukkit) guiHolder.getViewManager().getWolfyUtils().getCore()).getPlugin(), () -> {
                guiHolder.getViewManager().unblockedByInteraction();
                guiHolder.getViewManager().getRenderContext(event.getWhoClicked().getUniqueId()).ifPresent(context -> {
                    context.openAndRenderMenuFor(guiHolder.getViewManager(), player.getUniqueId());
                });
            });
        }
    }

    void onClose(InventoryCloseEvent event) {
        // TODO: Close Window
        if (currentWindow() == null) return;
        if (Objects.equals(event.getInventory().getHolder(), this)) {
            guiHolder.getViewManager().getCurrentMenu().ifPresent(window -> window.close(guiHolder.getViewManager()));
        }
    }

    void setActiveInventory(Inventory activeInventory) {
        this.activeInventory = activeInventory;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return activeInventory;
    }
}
