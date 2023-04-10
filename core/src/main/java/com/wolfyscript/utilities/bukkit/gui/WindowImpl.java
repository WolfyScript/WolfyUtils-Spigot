package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.ButtonBuilder;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.InteractionCallback;
import com.wolfyscript.utilities.common.gui.InteractionDetails;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.RenderCallback;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.Router;
import com.wolfyscript.utilities.common.gui.RouterBuilder;
import com.wolfyscript.utilities.common.gui.RouterState;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.SizedComponent;
import com.wolfyscript.utilities.common.gui.Window;
import com.wolfyscript.utilities.common.gui.WindowBuilder;
import com.wolfyscript.utilities.common.gui.WindowChildComponentBuilder;
import com.wolfyscript.utilities.common.gui.WindowState;
import com.wolfyscript.utilities.common.gui.WindowTitleUpdateCallback;
import com.wolfyscript.utilities.common.gui.WindowType;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public final class WindowImpl implements Window {

    private final WolfyUtils wolfyUtils;
    private final String id;
    private final Router parent;
    private final Map<String, Signal<?>> signals;
    private final BiMap<String, SizedComponent> children;
    private final Integer size;
    private final WindowType type;
    private final WindowTitleUpdateCallback titleUpdateCallback;
    private final InteractionCallback interactionCallback;
    private final RenderCallback<WindowState> renderCallback;

    private WindowImpl(String id,
                       Router parent,
                       WindowTitleUpdateCallback titleUpdateCallback,
                       Integer size, WindowType type,
                       InteractionCallback interactionCallback,
                       RenderCallback<WindowState> renderCallback,
                       Map<String, Signal<?>> signals) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(interactionCallback);
        Preconditions.checkNotNull(renderCallback);
        Preconditions.checkArgument(size != null || type != null, "Either type or size must be specified!");
        this.id = id;
        this.parent = parent;
        this.titleUpdateCallback = titleUpdateCallback;
        this.wolfyUtils = parent.getWolfyUtils();
        this.interactionCallback = interactionCallback;
        this.renderCallback = renderCallback;
        this.children = HashBiMap.create();
        this.size = size;
        this.type = type;
        this.signals = signals == null ? new HashMap<>() : signals;
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
        RenderContextImpl context = new RenderContextImpl(inventory);
        context.pushParentOnPath(this);
        return context;
    }

    @Override
    public WindowState createState(ComponentState state) {
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
    public void render(GuiHolder holder, WindowState state, RenderContext context) {

    }

    @Override
    public InteractionCallback interactCallback() {
        return interactionCallback;
    }

    @Override
    public RenderCallback<WindowState> renderCallback() {
        return renderCallback;
    }

    @Override
    public WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public Set<? extends SizedComponent> childComponents() {
        return children.values();
    }

    @Override
    public Router parent() {
        return parent;
    }

    @Override
    public Optional<com.wolfyscript.utilities.common.gui.Component> getChild(String id) {
        return Optional.ofNullable(children.get(id));
    }

    @Override
    public void init() {

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
    public net.kyori.adventure.text.Component createTitle(GuiHolder holder) {
        return titleUpdateCallback.run(holder, this);
    }

    @Override
    public int width() {
        return size / height();
    }

    @Override
    public int height() {
        return size / 9;
    }

    void addNewChildComponent(String id, SizedComponent component) {
        this.children.put(id, component);
    }

    public static class BuilderImpl implements WindowBuilder, ComponentBuilder<Window, Router> {

        private final WolfyUtils wolfyUtils;
        protected final String windowID;
        protected final RouterBuilder parent;
        protected final WindowChildComponentBuilder childComponentBuilder;
        protected Integer size;
        protected WindowType type;
        protected WindowTitleUpdateCallback titleUpdateCallback;
        private InteractionCallback interactionCallback = (guiHolder, componentState, interactionDetails) -> InteractionResult.def();
        private RenderCallback<WindowState> renderCallback = (guiHolder, componentState) -> {
        };
        private final Map<String, Signal<?>> signals;

        protected BuilderImpl(WolfyUtils wolfyUtils, String windowID, RouterImpl.Builder parent) {
            this.wolfyUtils = wolfyUtils;
            this.windowID = windowID;
            this.parent = parent;
            this.childComponentBuilder = new ChildBuilderImpl();
            this.signals = new HashMap<>();
        }

        @Override
        public WindowBuilder size(int size) {
            this.size = size;
            return this;
        }

        @Override
        public WindowBuilder type(WindowType type) {
            this.type = type;
            return this;
        }

        @Override
        public WindowBuilder title(WindowTitleUpdateCallback titleUpdateCallback) {
            this.titleUpdateCallback = titleUpdateCallback;
            return this;
        }

        @Override
        public <T> WindowBuilder useSignal(String key, Class<T> type, Consumer<Signal.Builder<T>> signalBuilder) {
            SignalImpl.Builder<T> builder = new SignalImpl.Builder<>(key, type);
            signalBuilder.accept(builder);
            this.signals.put(key, builder.create());
            return this;
        }

        public WindowBuilder children(Consumer<WindowChildComponentBuilder> childComponentBuilderConsumer) {
            childComponentBuilderConsumer.accept(childComponentBuilder);
            return this;
        }

        @Override
        public Window create(Router parent) {
            Window window = new WindowImpl(parent.getID() + "/" + windowID, parent, this.titleUpdateCallback, size, type, interactionCallback, renderCallback, signals);
            childComponentBuilder.applyTo(window);
            return window;
        }

        @Override
        public WindowBuilder interact(InteractionCallback interactionCallback) {
            Preconditions.checkNotNull(interactionCallback);
            this.interactionCallback = interactionCallback;
            return this;
        }

        @Override
        public WindowBuilder render(RenderCallback<WindowState> renderCallback) {
            Preconditions.checkNotNull(renderCallback);
            this.renderCallback = renderCallback;
            return this;
        }

        public class ChildBuilderImpl implements WindowChildComponentBuilder {

            private final BiMap<String, ComponentBuilder<? extends SizedComponent, SizedComponent>> children = HashBiMap.create();

            ChildBuilderImpl() {
                super();
            }

            @Override
            public WindowChildComponentBuilder button(String buttonId, Consumer<ButtonBuilder> consumer) {
                if (children.containsKey(buttonId)) {
                    if (!(children.get(buttonId) instanceof ButtonImpl.Builder builder))
                        throw new IllegalArgumentException("A builder for '" + buttonId + "' of a different type already exists!");
                    consumer.accept(builder);
                    return this;
                }
                ButtonImpl.Builder builder = new ButtonImpl.Builder(buttonId, BuilderImpl.this.wolfyUtils);
                children.putIfAbsent(buttonId, builder);
                consumer.accept(builder);
                return this;
            }

            @Override
            public void applyTo(Window window) {
                if (!(window instanceof WindowImpl thisWindow)) return;

                children.forEach((s, componentBuilder) -> {
                    thisWindow.addNewChildComponent(s, componentBuilder.create(thisWindow));
                });

            }
        }

    }

}
