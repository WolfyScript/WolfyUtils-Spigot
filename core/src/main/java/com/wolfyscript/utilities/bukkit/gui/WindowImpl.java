package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.common.gui.Cluster;
import com.wolfyscript.utilities.common.gui.ComponentStateDefault;
import com.wolfyscript.utilities.common.gui.Data;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.RenderCallback;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.RenderPreCallback;
import com.wolfyscript.utilities.common.gui.SlotComponent;
import com.wolfyscript.utilities.common.gui.StateSelector;
import com.wolfyscript.utilities.common.gui.Window;
import com.wolfyscript.utilities.common.gui.WindowCommonImpl;
import com.wolfyscript.utilities.common.gui.WindowState;
import com.wolfyscript.utilities.common.gui.WindowStateBuilder;
import com.wolfyscript.utilities.common.gui.WindowTitleUpdateCallback;
import com.wolfyscript.utilities.common.gui.WindowType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class WindowImpl<D extends Data> extends WindowCommonImpl<D> {

    protected WindowImpl(String id, Cluster<D> parent, WindowTitleUpdateCallback<D> titleUpdateCallback, StateSelector<D> stateSelector, WindowState<D>[] states, BiMap<String, ? extends SlotComponent<D>> children, Integer size, WindowType type) {
        super(id, parent, titleUpdateCallback, stateSelector, states, children, size, type);
    }

    @SuppressWarnings("unchecked")
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
            if (Objects.equals(holder.getCurrentWindow(), this)) {
                // Still in the same window, we can just update it.
                RenderContext<D> renderContext = new RenderContextImpl<>(topInventory);
                handler.getRoot().render((GuiHolder<D>) holder, handler.getData(), renderContext);
                return;
            }
        }
        // No active Window or it is another Window, need to recreate inventory
        final Inventory inventory;
        final GUIHolder<D> holder = new GUIHolder<>(bukkitPlayer, handler, this);
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

        RenderContext<D> renderContext = new RenderContextImpl<>(inventory);
        handler.getRoot().render(holder, handler.getData(), renderContext);
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
        public Builder<D> state(Consumer<WindowStateBuilder<D>> stateBuilderConsumer) {
            WindowStateBuilder<D> stateBuilder = null;
            stateBuilderConsumer.accept(stateBuilder);
            stateBuilders.add(stateBuilder);
            return this;
        }

        @Override
        protected Window<D> constructImplementation(String s, Cluster<D> cluster, StateSelector<D> stateSelector, WindowState<D>[] componentStates, BiMap<String, ? extends SlotComponent<D>> children, Integer size, WindowType type) {
            return new WindowImpl<>(s, cluster, this.titleUpdateCallback, stateSelector, componentStates, children, size, type);
        }
    }

    public static class State<D extends Data> extends ComponentStateDefault<D> {

        public Map<Integer, SlotComponent<D>> componentPositions;

        protected State(String key, Map<Integer, SlotComponent<D>> childrenPositions, InteractionCallback<D> interactionCallback, RenderPreCallback<D> renderPreCallback, RenderCallback<D> renderCallback) {
            super(key, interactionCallback, renderPreCallback, renderCallback);
            this.componentPositions = Map.copyOf(childrenPositions);
        }

        public Map<Integer, SlotComponent<D>> getComponentPositions() {
            return componentPositions;
        }

        public static class Builder<D extends Data> extends ComponentStateDefault.Builder<D> {

            public Map<Integer, String> childrenPositions = new HashMap<>();
            public Map<Integer, String[]> componentPositions = new HashMap<>();

            protected Builder(String ownerID) {
                super(ownerID);
            }

            public Builder<D> childSlot(int slot, String childComponentId) {
                childrenPositions.put(slot, childComponentId);
                return this;
            }

            public Builder<D> componentSlot(int slot, String... pathFromRoot) {
                Preconditions.checkArgument(pathFromRoot != null && pathFromRoot.length > 0, "Path to component cannot be empty or null!");
                componentPositions.put(slot, pathFromRoot);
                return this;
            }

            @Override
            public ComponentStateDefault<D> create() {
                // TODO
                return super.create();
            }
        }

    }

    public static class ChildBuilderImpl<D extends Data> extends WindowCommonImpl.ChildBuilder<D> {

        protected ChildBuilderImpl(Cluster<D> parent) {
            super(parent);
        }
    }

}
