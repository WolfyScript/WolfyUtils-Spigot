package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.bukkit.gui.button.Button;
import com.wolfyscript.utilities.common.gui.Data;
import com.wolfyscript.utilities.common.gui.GuiHolderCommonImpl;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.Window;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class GUIHolder<D extends Data> extends GuiHolderCommonImpl<D> implements InventoryHolder {

    private Inventory activeInventory;
    private final Player player;

    public GUIHolder(Player player, GuiViewManager<D> viewManager, Window<D> window) {
        super(window, viewManager);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    void onClick(InventoryClickEvent event) {
        if (currentWindow == null || event.getClickedInventory() == null) return;
        if (Objects.equals(event.getClickedInventory().getHolder(), this)) {
            Optional<ComponentStateNode<D>> nodeOptional = ((GuiViewManagerImpl<D>) viewManager).getTailNode(event.getSlot());
            var interactionDetails = new ClickInteractionDetailsImpl<D>(event);
            var data = viewManager.getData();
            InteractionResult result = nodeOptional.map(node -> node.getOwner().interact(this, data, interactionDetails)).orElse(InteractionResult.def());
            // TODO: Way of adding tail components to call interact on? For example Item Input slots.
            event.setCancelled(result.isCancelled());
        } else if (!event.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
            event.setCancelled(false);
            // TODO: Handle bottom inventory clicks
        }
        // TODO: update window & necessary components
        currentWindow.open(viewManager, player.getUniqueId());
    }

    void onDrag(InventoryDragEvent event) {
        if (event.getRawSlots().parallelStream().anyMatch(rawSlot -> !Objects.equals(event.getView().getInventory(rawSlot), activeInventory))) {
            event.setCancelled(true);
            return;
        }
        if (currentWindow == null) return;
        if (Objects.equals(event.getInventory().getHolder(), this)) {
            var interactionDetails = new DragInteractionDetailsImpl<D>(event);
            var data = viewManager.getData();
            for (int slot : event.getInventorySlots()) {
                if (((GuiViewManagerImpl<D>) viewManager).getTailNode(slot).map(node -> node.getOwner().interact(this, data, interactionDetails)).orElse(InteractionResult.def()).isCancelled()) {
                    event.setCancelled(true);
                }
            }
            currentWindow.open(viewManager, player.getUniqueId());
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
