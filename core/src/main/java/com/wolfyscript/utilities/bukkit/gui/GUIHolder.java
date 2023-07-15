package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.adapters.PlayerImpl;
import com.wolfyscript.utilities.common.gui.*;
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

public class GUIHolder extends GuiHolderCommonImpl implements InventoryHolder {

    private Inventory activeInventory;
    private final Player player;

    public GUIHolder(Player player, GuiViewManager viewManager, Window window) {
        super(window, viewManager);
        this.player = player;
    }

    public Player getBukkitPlayer() {
        return player;
    }

    @Override
    public com.wolfyscript.utilities.common.adapters.Player getPlayer() {
        return new PlayerImpl(player);
    }

    void onClick(InventoryClickEvent event) {
        if (currentWindow == null || event.getClickedInventory() == null) return;
        if (Objects.equals(event.getClickedInventory().getHolder(), this)) {
            GuiViewManagerImpl guiViewManager = (GuiViewManagerImpl) viewManager;
            viewManager.getCurrentMenu().ifPresent(window -> {
                InteractionResult result = guiViewManager.getLeaveNode(event.getSlot())
                        .map(component -> {
                            if (component instanceof Interactable interactable) {
                                return interactable.interact(this, null, new ClickInteractionDetailsImpl(event));
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
        Bukkit.getScheduler().runTask(((WolfyCoreBukkit) viewManager.getWolfyUtils().getCore()).getPlugin(), () -> {
            viewManager.getRenderContext(event.getWhoClicked().getUniqueId()).ifPresent(context -> {
                ((GuiViewManagerImpl) viewManager).renderFor(player, (RenderContextImpl) context);
            });
        });
    }

    void onDrag(InventoryDragEvent event) {
        if (event.getRawSlots().stream().anyMatch(rawSlot -> !Objects.equals(event.getView().getInventory(rawSlot), activeInventory))) {
            event.setCancelled(true);
            return;
        }
        if (currentWindow == null) return;
        if (Objects.equals(event.getInventory().getHolder(), this)) {
            var interactionDetails = new DragInteractionDetailsImpl(event);
            for (int slot : event.getInventorySlots()) {
                if (((GuiViewManagerImpl) viewManager).getLeaveNode(slot).map(component -> {
                            if (component instanceof Interactable interactable) {
                                return interactable.interact(this, null, interactionDetails);
                            }
                            return InteractionResult.cancel(true);
                        }).orElse(InteractionResult.cancel(true)).isCancelled()) {
                    event.setCancelled(true);
                }
            }

            Bukkit.getScheduler().runTask(((WolfyCoreBukkit) viewManager.getWolfyUtils().getCore()).getPlugin(), () -> {
                viewManager.getRenderContext(event.getWhoClicked().getUniqueId()).ifPresent(context -> {
                    ((GuiViewManagerImpl) viewManager).renderFor(player, (RenderContextImpl) context);
                });
            });
        }
    }

    void onClose(InventoryCloseEvent event) {
        // TODO: Close Window
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
