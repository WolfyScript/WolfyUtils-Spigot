package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.common.WolfyUtils;
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
import com.wolfyscript.utilities.common.gui.SizedComponent;
import com.wolfyscript.utilities.common.gui.Window;
import com.wolfyscript.utilities.common.gui.WindowChildComponentBuilder;
import com.wolfyscript.utilities.common.gui.WindowComponentBuilder;
import com.wolfyscript.utilities.common.gui.WindowTitleUpdateCallback;
import com.wolfyscript.utilities.common.gui.WindowType;
import java.util.Deque;
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
    private final BiMap<String, SizedComponent> children;
    private final Integer size;
    private final WindowType type;
    private final WindowTitleUpdateCallback titleUpdateCallback;
    private final InteractionCallback interactionCallback;
    private final RenderCallback renderCallback;

    private WindowImpl(String id, Router parent, WindowTitleUpdateCallback titleUpdateCallback, Integer size, WindowType type, InteractionCallback interactionCallback, RenderCallback renderCallback) {
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
                RenderContext renderContext = new RenderContextImpl<>(topInventory);
                ComponentState rootState = ((GuiViewManagerImpl) holder.getViewManager()).getRootStateNode();
                // TODO: Update window even if no state has changed? probably not!?
                if (rootState.shouldUpdate()) {
                    rootState.render(holder, renderContext);
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
            ComponentStateRouterImpl rootState = new ComponentStateRouterImpl(null, handler.getRoot());
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
    public Class<? extends com.wolfyscript.utilities.common.gui.ComponentStateWindow> getComponentStateType() {
        return ComponentStateWindowImpl.class;
    }

    @Override
    public InteractionCallback interactCallback() {
        return interactionCallback;
    }

    @Override
    public RenderCallback renderCallback() {
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

    public static class BuilderImpl implements WindowComponentBuilder {

        protected final String windowID;
        protected final RouterBuilder parent;
        protected final WindowChildComponentBuilder childComponentBuilder;
        protected Integer size;
        protected WindowType type;
        protected WindowTitleUpdateCallback titleUpdateCallback;
        private InteractionCallback interactionCallback = (guiHolder, componentState, interactionDetails) -> InteractionResult.cancel(true);
        private RenderCallback renderCallback = (guiHolder, componentState) -> {};

        protected BuilderImpl(String windowID, RouterImpl.Builder parent) {
            this.windowID = windowID;
            this.parent = parent;
            this.childComponentBuilder = new ChildBuilderImpl();
        }

        @Override
        public WindowComponentBuilder size(int size) {
            this.size = size;
            return this;
        }

        @Override
        public WindowComponentBuilder type(WindowType type) {
            this.type = type;
            return this;
        }

        @Override
        public WindowComponentBuilder title(WindowTitleUpdateCallback titleUpdateCallback) {
            this.titleUpdateCallback = titleUpdateCallback;
            return this;
        }

        public WindowComponentBuilder children(Consumer<WindowChildComponentBuilder> childComponentBuilderConsumer) {
            childComponentBuilderConsumer.accept(childComponentBuilder);
            return this;
        }

        @Override
        public Window create(Router parent) {
            Window window = new WindowImpl(parent.getID() + "/" + windowID, parent, this.titleUpdateCallback, size, type, interactionCallback, renderCallback);
            childComponentBuilder.applyTo(window);
            return window;
        }

        @Override
        public WindowComponentBuilder interact(InteractionCallback interactionCallback) {
            Preconditions.checkNotNull(interactionCallback);
            this.interactionCallback = interactionCallback;
            return this;
        }

        @Override
        public WindowComponentBuilder render(RenderCallback renderCallback) {
            Preconditions.checkNotNull(renderCallback);
            this.renderCallback = renderCallback;
            return this;
        }

        public static class ChildBuilderImpl implements WindowChildComponentBuilder {

            ChildBuilderImpl() {
                super();
            }

            @Override
            public BiMap<String, ? extends SizedComponent> create() {
                return null;
            }

            @Override
            public void applyTo(Window window) {
                if (!(window instanceof WindowImpl parent)) return;

            }
        }

    }

}
