package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.adapters.BukkitWrapper;
import com.wolfyscript.utilities.gui.*;
import com.wolfyscript.utilities.platform.gui.GuiUtils;
import net.kyori.adventure.text.serializer.craftbukkit.BukkitComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class GuiUtilsImpl implements GuiUtils {

    @Override
    public RenderContext createRenderContext(Window window, ViewRuntime viewManager, UUID viewer) {
        Player bukkitPlayer = Bukkit.getPlayer(viewer);
        if (bukkitPlayer == null) return null;
        Inventory topInventory = bukkitPlayer.getOpenInventory().getTopInventory();

        if (topInventory.getHolder() instanceof BukkitInventoryGuiHolder inventoryGuiHolder) {
            if (Objects.equals(inventoryGuiHolder.guiHolder().getCurrentWindow(), window)) {
                // Still in the same window, we can just update it.
                return new RenderContextImpl(inventoryGuiHolder.guiHolder(), topInventory, viewManager.getRouter(), inventoryGuiHolder.guiHolder().getCurrentWindow());
            }
        }

        // No active Window or it is another Window, need to recreate inventory
        final Inventory inventory;
        final GuiHolder guiHolder = new GuiHolderImpl(window, viewManager, BukkitWrapper.adapt(bukkitPlayer));
        final BukkitInventoryGuiHolder holder = new BukkitInventoryGuiHolder(bukkitPlayer, guiHolder);
        final net.kyori.adventure.text.Component title = window.createTitle(guiHolder);

        if (((WolfyUtilsBukkit) window.getWolfyUtils()).getCore().getCompatibilityManager().isPaper()) {
            // Paper has direct Adventure support, so use it for better titles!
            inventory = getInventoryType(window).map(inventoryType -> Bukkit.createInventory(holder, inventoryType, title))
                    .orElseGet(() -> Bukkit.createInventory(holder, window.getSize().orElseThrow(() -> new IllegalStateException("Invalid window type/size definition.")), title));
        } else {
            inventory = getInventoryType(window).map(inventoryType -> Bukkit.createInventory(holder, inventoryType, BukkitComponentSerializer.legacy().serialize(title)))
                    .orElseGet(() -> Bukkit.createInventory(holder, window.getSize().orElseThrow(() -> new IllegalStateException("Invalid window type/size definition.")), BukkitComponentSerializer.legacy().serialize(title)));
        }
        holder.setActiveInventory(inventory);
        return new RenderContextImpl(guiHolder, inventory, viewManager.getRouter(), window);
    }

    private Optional<InventoryType> getInventoryType(Window window) {
        return window.getType().map(type -> switch (type) {
            case CUSTOM -> InventoryType.CHEST;
            case HOPPER -> InventoryType.HOPPER;
            case DROPPER -> InventoryType.DROPPER;
            case DISPENSER -> InventoryType.DISPENSER;
        });
    }
}
