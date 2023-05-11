package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.InteractionDetails;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.RenderCallback;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.components.Router;
import com.wolfyscript.utilities.common.gui.components.RouterState;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.SizedComponent;
import com.wolfyscript.utilities.common.gui.components.Window;
import com.wolfyscript.utilities.common.gui.components.WindowChildComponentBuilder;
import com.wolfyscript.utilities.common.gui.components.WindowState;
import com.wolfyscript.utilities.common.gui.components.WindowTitleUpdateCallback;
import com.wolfyscript.utilities.common.gui.WindowType;
import com.wolfyscript.utilities.common.gui.components.CallbackInitComponent;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

@KeyedStaticId(key = "window")
public final class WindowImpl extends AbstractBukkitComponent implements Window {

    private final Map<String, Signal<?>> signals;
    private final BiMap<String, SizedComponent> children;
    private final Integer size;
    private final WindowType type;
    private final WindowTitleUpdateCallback titleUpdateCallback;
    private final InteractionCallback interactionCallback;
    private final CallbackInitComponent initCallback;

    WindowImpl(String id,
               Router parent,
               Integer size,
               WindowType type,
               WindowTitleUpdateCallback titleUpdateCallback,
               InteractionCallback interactionCallback,
               WindowChildComponentBuilder childComponentBuilder,
               CallbackInitComponent initCallback,
               Map<String, Signal<?>> signals) {
        super(id, parent.getWolfyUtils(), parent);
        this.initCallback = initCallback;
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(interactionCallback);
        Preconditions.checkArgument(size != null || type != null, "Either type or size must be specified!");
        this.size = size;
        this.type = type;
        this.titleUpdateCallback = titleUpdateCallback;
        this.interactionCallback = interactionCallback;
        this.children = HashBiMap.create();
        childComponentBuilder.applyTo(this);
        this.signals = signals == null ? new HashMap<>() : signals;
    }

    void addNewChildComponent(String id, SizedComponent component) {
        this.children.put(id, component);
    }

    @Override
    public Router parent() {
        return (Router) super.parent();
    }

    @Override
    public void init() {

    }

    @Override
    public RenderContext createContext(GuiViewManager viewManager, Deque<String> path, UUID player) {
        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer == null) return null;
        Inventory topInventory = bukkitPlayer.getOpenInventory().getTopInventory();

        if (topInventory.getHolder() instanceof GUIHolder holder) {
            if (Objects.equals(holder.getCurrentWindow(), this)) {
                // Still in the same window, we can just update it.
                RenderContextImpl context = new RenderContextImpl(topInventory);
                context.pushParentOnPath(this);
                return context;
            }
        }
        // No active Window or it is another Window, need to recreate inventory
        final Inventory inventory;
        final GUIHolder holder = new GUIHolder(bukkitPlayer, viewManager, this);
        final net.kyori.adventure.text.Component title = createTitle(holder, null);
        if (((WolfyUtilsBukkit) getWolfyUtils()).getCore().getCompatibilityManager().isPaper()) {
            // Paper has direct Adventure support, so use it for better titles!
            inventory = getInventoryType().map(inventoryType -> Bukkit.createInventory(holder, inventoryType, title))
                    .orElseGet(() -> Bukkit.createInventory(holder, getSize().orElseThrow(() -> new IllegalStateException("Invalid window type/size definition.")), title));
        } else {
            inventory = getInventoryType().map(inventoryType -> Bukkit.createInventory(holder, inventoryType, BukkitComponentSerializer.legacy().serialize(title)))
                    .orElseGet(() -> Bukkit.createInventory(holder, getSize().orElseThrow(() -> new IllegalStateException("Invalid window type/size definition.")), BukkitComponentSerializer.legacy().serialize(title)));
        }
        holder.setActiveInventory(inventory);
        RenderContextImpl context = new RenderContextImpl(inventory);
        context.pushParentOnPath(this);
        return context;
    }

    @Override
    public WindowState createState(ComponentState state, GuiHolder holder) {
        if (!(state instanceof RouterState parentState))
            throw new IllegalArgumentException("Cannot create window state without a router parent!");
        return new WindowStateImpl(parentState, this);
    }

    @Override
    public Map<String, Signal<?>> signals() {
        return signals;
    }

    @Override
    public void open(GuiViewManager viewManager, RouterState parentState, Deque<String> path, UUID player) {

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
    public InteractionCallback interactCallback() {
        return interactionCallback;
    }

    @Override
    public Set<? extends SizedComponent> childComponents() {
        return children.values();
    }

    @Override
    public Optional<com.wolfyscript.utilities.common.gui.Component> getChild(String id) {
        return Optional.ofNullable(children.get(id));
    }

    @Override
    public Optional<Integer> getSize() {
        return Optional.ofNullable(size);
    }

    @Override
    public Optional<WindowType> getType() {
        return Optional.ofNullable(type);
    }

    @Override
    public net.kyori.adventure.text.Component createTitle(GuiHolder holder, WindowState state) {
        return titleUpdateCallback.run(holder, this, state);
    }

    @Override
    public int width() {
        return size / height();
    }

    @Override
    public int height() {
        return size / 9;
    }

    @Override
    public CallbackInitComponent getInitCallback() {
        return initCallback;
    }

    public static class RenderOptionsImpl implements RenderOptions {

        private final RenderCallback<WindowState> renderCallback;
        private final Map<Integer, Component> placement;
        private final Map<Component, int[]> reversePlacement;

        public RenderOptionsImpl(RenderCallback<WindowState> renderCallback, Map<Integer, ? extends Component> placement) {
            this.renderCallback = renderCallback;

            this.placement = Collections.unmodifiableMap(placement);
            this.reversePlacement = new HashMap<>();
            for (Map.Entry<Integer, ? extends Component> entry : placement.entrySet()) {
                reversePlacement.merge(entry.getValue(), new int[]{entry.getKey()}, ArrayUtils::addAll);
            }
        }

        @Override
        public Optional<RenderCallback<WindowState>> renderCallback() {
            return Optional.ofNullable(renderCallback);
        }

        @Override
        public Map<Integer, ? extends Component> placement() {
            return placement;
        }

        @Override
        public int[] getSlotsFor(Component component) {
            return reversePlacement.getOrDefault(component, new int[0]);
        }
    }

}
