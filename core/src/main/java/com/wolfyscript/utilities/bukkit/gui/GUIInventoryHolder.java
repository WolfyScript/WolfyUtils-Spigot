package com.wolfyscript.utilities.bukkit.gui;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class GUIInventoryHolder<C extends CustomCache> implements InventoryHolder {

    private final GuiWindow<C> window;
    private final GuiHandler<C> guiHandler;

    public GUIInventoryHolder(GuiHandler<C> guiHandler, GuiWindow<C> window) {
        this.guiHandler = guiHandler;
        this.window = window;
    }

    public GuiHandler<C> getGuiHandler() {
        return guiHandler;
    }

    public GuiWindow<C> getWindow() {
        return window;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return guiHandler.getPlayer().getInventory();
    }
}
