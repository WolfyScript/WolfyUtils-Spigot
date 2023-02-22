package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.InteractionDetails;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.RenderCallback;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.RenderPreCallback;
import com.wolfyscript.utilities.common.gui.Router;
import com.wolfyscript.utilities.common.gui.SlotComponent;
import com.wolfyscript.utilities.common.gui.StateSelector;
import com.wolfyscript.utilities.common.gui.Window;
import com.wolfyscript.utilities.common.gui.WindowCommonImpl;
import com.wolfyscript.utilities.common.gui.WindowState;
import com.wolfyscript.utilities.common.gui.WindowStateBuilder;
import com.wolfyscript.utilities.common.gui.WindowTitleUpdateCallback;
import com.wolfyscript.utilities.common.gui.WindowType;
import java.util.Deque;
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

public class WindowImpl extends WindowCommonImpl {

    protected WindowImpl(String id, Router parent, WindowTitleUpdateCallback titleUpdateCallback, BiMap<String, ? extends SlotComponent> children, Integer size, WindowType type) {
        super(id, parent, titleUpdateCallback, children, size, type);
    }

    @Override
    public void open(GuiViewManager handler, UUID player) {
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
        Deque<com.wolfyscript.utilities.common.gui.Component> pathToRoot = getPathToRoot();
        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer == null) return;
        Inventory topInventory = bukkitPlayer.getOpenInventory().getTopInventory();
        if (topInventory.getHolder() instanceof GUIHolder holder) {
            if (Objects.equals(holder.getCurrentWindow(), this)) {
                // Still in the same window, we can just update it.
                RenderContext renderContext = new RenderContextImpl(topInventory);
                ComponentState rootState = ((GuiViewManagerImpl) holder.getViewManager()).getRootStateNode();
                // TODO: Update window even if no state has changed? probably not!?
                if (rootState.shouldUpdate()) {
                    handler.getRoot().render(holder, rootState, renderContext);
                }
                return;
            }
        }
        // No active Window or it is another Window, need to recreate inventory
        final Inventory inventory;
        final GUIHolder holder = new GUIHolder(bukkitPlayer, handler, this);
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

        RenderContext renderContext = new RenderContextImpl(inventory);
        GuiViewManagerImpl viewManager = ((GuiViewManagerImpl) holder.getViewManager());

        if (!handler.getRoot().equals(pathToRoot.pop())) {
            // This should not happen. Consider InvalidStateException
            return;
        }

        // Create the state tree!
        if (viewManager.getRootStateNode() == null || !viewManager.getRootStateNode().getOwner().equals(handler.getRoot())) {
            ComponentStateImpl rootState = new ComponentStateImpl(null, handler.getRoot());
            rootState.render(holder, renderContext);
            viewManager.changeRootState(rootState);
        } else {
            viewManager.getRootStateNode().render(holder, renderContext);
        }

    }

    private Optional<InventoryType> getInventoryType() {
        return getType().map(type -> switch (type) {
            case CUSTOM -> InventoryType.CHEST;
            case HOPPER -> InventoryType.HOPPER;
            case DROPPER -> InventoryType.DROPPER;
            case DISPENSER -> InventoryType.DISPENSER;
        });
    }

    @Override
    public InteractionResult interact(GuiHolder holder, ComponentState state, InteractionDetails interactionDetails) {
        return null;
    }

    @Override
    public void render(GuiHolder holder, ComponentState state, RenderContext context) {

    }

    @Override
    public InteractionCallback interactCallback() {
        return null;
    }

    @Override
    public RenderCallback renderCallback() {
        return null;
    }

    public static class BuilderImpl extends WindowCommonImpl.Builder {

        protected BuilderImpl(String subID, Router parent) {
            super(subID, parent, new WindowImpl.ChildBuilderImpl(parent));
        }

        @Override
        protected Window constructImplementation(String s, Router router, BiMap<String, ? extends SlotComponent> children, Integer size, WindowType type) {
            return new WindowImpl(s, router, this.titleUpdateCallback, children, size, type);
        }
    }

    public static class ChildBuilderImpl extends WindowCommonImpl.ChildBuilder {

        protected ChildBuilderImpl(Router parent) {
            super(parent);
        }
    }

}
