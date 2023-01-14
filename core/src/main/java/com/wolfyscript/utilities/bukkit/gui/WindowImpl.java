package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.collect.BiMap;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.common.gui.Cluster;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.Data;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.SlotComponent;
import com.wolfyscript.utilities.common.gui.StateSelector;
import com.wolfyscript.utilities.common.gui.Window;
import com.wolfyscript.utilities.common.gui.WindowCommonImpl;
import com.wolfyscript.utilities.common.gui.WindowType;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class WindowImpl<D extends Data> extends WindowCommonImpl<D> {

    protected WindowImpl(String id, Cluster<D> parent, StateSelector<D> stateSelector, ComponentState<D>[] states, BiMap<String, ? extends SlotComponent<D>> children, Integer size, WindowType type) {
        super(id, parent, stateSelector, states, children, size, type);
    }

    @Override
    public void open(GuiViewManager<D> handler, UUID player) {
        /* TODO:
         if player has an open inv with holder:
          - Check if window is the same.
           is the same window:
            - Use existing holder and update.
         otherwise:
          - Otherwise create new GuiHolder for this window.
          - Create inventory and set holder.
          - open inventory.
         */
        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer == null) return;
        Inventory topInventory = bukkitPlayer.getOpenInventory().getTopInventory();
        if (topInventory.getHolder() instanceof GUIHolder<?> holder) {
            if (Objects.equals(holder.getWindow(), this)) {
                // Still in the same window, we can just update it.

                return;
            }
        }
        final Inventory inventory;
        final GUIHolder<D> holder = new GUIHolder<D>(bukkitPlayer, handler, this);
        final Component title = createTitle(holder);
        if (((WolfyUtilsBukkit) getWolfyUtils()).getCore().getCompatibilityManager().isPaper()) {
            // Paper has direct Adventure support, so use it for better titles!
            inventory = getInventoryType().map(inventoryType -> Bukkit.createInventory(holder, inventoryType, title))
                    .orElseGet(() -> Bukkit.createInventory(holder, getSize().orElseThrow(() -> new IllegalStateException("Invalid window type/size definition.")), title));
        } else {
            inventory = getInventoryType().map(inventoryType -> Bukkit.createInventory(holder, inventoryType, BukkitComponentSerializer.legacy().serialize(title)))
                    .orElseGet(() -> Bukkit.createInventory(holder, getSize().orElseThrow(() -> new IllegalStateException("Invalid window type/size definition.")), BukkitComponentSerializer.legacy().serialize(title)));
        }
        holder.setActiveInventory(inventory);
    }

    private Optional<InventoryType> getInventoryType() {
        return getType().map(type -> switch (type) {
            case CUSTOM -> InventoryType.CHEST;
            case HOPPER -> InventoryType.HOPPER;
            case DROPPER -> InventoryType.DROPPER;
            case DISPENSER -> InventoryType.DISPENSER;
        });
    }

    public static class BuilderImpl<D extends Data> extends WindowCommonImpl.Builder<D> {

        protected BuilderImpl(String subID, Cluster<D> parent) {
            super(subID, parent, new WindowImpl.ChildBuilderImpl<>(parent));
        }

        @Override
        protected Window<D> constructImplementation(String s, Cluster<D> cluster, StateSelector<D> stateSelector, ComponentState<D>[] componentStates, BiMap<String, ? extends SlotComponent<D>> children, Integer size, WindowType type) {
            return new WindowImpl<>(s, cluster, stateSelector, componentStates, children, size, type);
        }
    }

    public static class ChildBuilderImpl<D extends Data> extends WindowCommonImpl.ChildBuilder<D> {

        protected ChildBuilderImpl(Cluster<D> parent) {
            super(parent);
        }
    }

}
