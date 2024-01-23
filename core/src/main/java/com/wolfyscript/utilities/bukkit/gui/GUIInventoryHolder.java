package com.wolfyscript.utilities.bukkit.gui;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * An InventoryHolder implementation that keeps track of the GUI that belongs to an Inventory.
 * This replaces the old GUIInventory, that was generated via NMS.
 *
 * @param <C> The CustomCache type
 */
public class GUIInventoryHolder<C extends CustomCache> implements InventoryHolder {

    private final GuiWindow<C> window;
    private final GuiHandler<C> guiHandler;

    public GUIInventoryHolder(GuiHandler<C> guiHandler, GuiWindow<C> window) {
        this.guiHandler = guiHandler;
        this.window = window;
    }

    public GuiHandler<C> guiHandler() {
        return guiHandler;
    }

    public GuiWindow<C> window() {
        return window;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return guiHandler.getPlayer().getInventory();
    }

    public void onClick(InventoryClickEvent event) {
        guiHandler.getInvAPI().onClick(guiHandler, new GUIInventoryDeferrer<>(getInventory(), window, guiHandler), event);
    }

    public void onDrag(InventoryDragEvent event) {
        guiHandler.getInvAPI().onDrag(guiHandler, new GUIInventoryDeferrer<>(getInventory(), window, guiHandler), event);
    }

    public void onClose(InventoryCloseEvent event) {
        guiHandler.onClose(new GUIInventoryDeferrer<>(getInventory(), window, guiHandler), event);
    }
}
