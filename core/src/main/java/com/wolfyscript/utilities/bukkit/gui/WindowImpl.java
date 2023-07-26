package com.wolfyscript.utilities.bukkit.gui;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.WolfyCoreImpl;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.nms.inventory.InventoryUpdate;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.*;

import java.util.*;
import java.util.function.Consumer;

import com.wolfyscript.utilities.common.gui.functions.SerializableSupplier;
import com.wolfyscript.utilities.versioning.MinecraftVersion;
import com.wolfyscript.utilities.versioning.ServerVersion;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

@KeyedStaticId(key = "window")
public final class WindowImpl implements Window {

    private final String id;
    private final Router router;
    private final WolfyUtils wolfyUtils;
    private final Consumer<com.wolfyscript.utilities.common.gui.WindowRenderer.Builder> rendererConstructor;
    private final Integer size;
    private final WindowType type;
    private String staticTitle = null;
    private SerializableSupplier<net.kyori.adventure.text.Component> dynamicTitle;
    private final InteractionCallback interactionCallback;
    final Multimap<Component, Integer> staticComponents;
    final Multimap<ComponentBuilder<?, ?>, Integer> nonRenderedComponents;

    WindowImpl(String id,
               Router router,
               Integer size,
               WindowType type,
               String staticTitle,
               InteractionCallback interactionCallback,
               Multimap<Component, Integer> staticComponents,
               Multimap<ComponentBuilder<?, ?>, Integer> nonRenderedComponents,
               Consumer<com.wolfyscript.utilities.common.gui.WindowRenderer.Builder> rendererConstructor) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(interactionCallback);
        Preconditions.checkArgument(size != null || type != null, "Either type or size must be specified!");
        this.id = id;
        this.router = router;
        this.wolfyUtils = router.getWolfyUtils();
        this.rendererConstructor = rendererConstructor;
        this.size = size;
        this.type = type;
        this.staticTitle = staticTitle;
        this.interactionCallback = interactionCallback;
        this.staticComponents = staticComponents;
        this.nonRenderedComponents = nonRenderedComponents;
        this.dynamicTitle = null;
    }

    public WindowImpl(WindowImpl staticWindow) {
        this.id = staticWindow.id;
        this.router = staticWindow.router;
        this.wolfyUtils = staticWindow.router.getWolfyUtils();
        this.rendererConstructor = staticWindow.rendererConstructor;
        this.size = staticWindow.size;
        this.type = staticWindow.type;
        this.staticTitle = staticWindow.staticTitle;
        this.dynamicTitle = staticWindow.dynamicTitle;
        this.interactionCallback = staticWindow.interactionCallback;
        this.staticComponents = MultimapBuilder.hashKeys().arrayListValues().build(staticWindow.staticComponents);
        this.nonRenderedComponents = MultimapBuilder.hashKeys().arrayListValues().build(staticWindow.nonRenderedComponents);
    }

    public WindowImpl dynamicCopy(Multimap<Component, Integer> dynamicComponents, Multimap<ComponentBuilder<?, ?>, Integer> nonRenderedComponents, SerializableSupplier<net.kyori.adventure.text.Component> dynamicTitle) {
        WindowImpl copy = new WindowImpl(this);
        copy.staticComponents.putAll(dynamicComponents);
        copy.nonRenderedComponents.putAll(nonRenderedComponents);
        copy.dynamicTitle = dynamicTitle;
        return copy;
    }

    @Override
    public Window construct(GuiViewManager viewManager) {
        var rendererBuilder = new WindowDynamicConstructor(wolfyUtils, viewManager, this);
        rendererConstructor.accept(rendererBuilder);
        return rendererBuilder.create(this);
    }

    @Override
    public void open(GuiViewManager guiViewManager) {

    }

    @Override
    public void render(GuiHolder guiHolder, GuiViewManager viewManager, RenderContext context) {
        if (!(context instanceof RenderContextImpl renderContext)) return;

        if (dynamicTitle != null) {
            if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0))) {
                ((GUIHolder) guiHolder).getBukkitPlayer().getOpenInventory().setTitle(BukkitComponentSerializer.legacy().serialize(dynamicTitle.get()));
            } else {
                InventoryUpdate.updateInventory(((WolfyCoreImpl) wolfyUtils.getCore()).getWolfyUtils().getPlugin(), ((GUIHolder) guiHolder).getBukkitPlayer(), dynamicTitle.get());
            }
        }

        for (Map.Entry<Component, Integer> entry : staticComponents.entries()) {
            int slot = entry.getValue();
            Component component = entry.getKey();
            renderContext.setSlotOffsetToParent(slot);
            ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(component, slot);
            renderContext.enterNode(component);
            if (component.construct(viewManager) instanceof SignalledObject signalledObject) {
                signalledObject.update(viewManager, guiHolder, renderContext);
            }
            renderContext.exitNode();
        }
    }

    @Override
    public WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public String getID() {
        return null;
    }

    @Override
    public Router router() {
        return router;
    }

    @Override
    public RenderContext createContext(GuiViewManager viewManager, UUID player) {
        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer == null) return null;
        Inventory topInventory = bukkitPlayer.getOpenInventory().getTopInventory();

        if (topInventory.getHolder() instanceof GUIHolder holder) {
            if (Objects.equals(holder.getCurrentWindow(), this)) {
                // Still in the same window, we can just update it.
                return new RenderContextImpl(topInventory, viewManager.getRouter(), this);
            }
        }
        // No active Window or it is another Window, need to recreate inventory
        final Inventory inventory;
        final GUIHolder holder = new GUIHolder(bukkitPlayer, viewManager, this);
        final net.kyori.adventure.text.Component title = createTitle(holder);
        if (((WolfyUtilsBukkit) getWolfyUtils()).getCore().getCompatibilityManager().isPaper()) {
            // Paper has direct Adventure support, so use it for better titles!
            inventory = getInventoryType().map(inventoryType -> Bukkit.createInventory(holder, inventoryType, title))
                    .orElseGet(() -> Bukkit.createInventory(holder, getSize().orElseThrow(() -> new IllegalStateException("Invalid window type/size definition.")), title));
        } else {
            inventory = getInventoryType().map(inventoryType -> Bukkit.createInventory(holder, inventoryType, BukkitComponentSerializer.legacy().serialize(title)))
                    .orElseGet(() -> Bukkit.createInventory(holder, getSize().orElseThrow(() -> new IllegalStateException("Invalid window type/size definition.")), BukkitComponentSerializer.legacy().serialize(title)));
        }
        holder.setActiveInventory(inventory);
        return new RenderContextImpl(inventory, viewManager.getRouter(), this);
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
    public InteractionResult interact(GuiHolder holder, InteractionDetails interactionDetails) {
        return null;
    }

    @Override
    public InteractionCallback interactCallback() {
        return interactionCallback;
    }

    @Override
    public Set<? extends Component> childComponents() {
        return Set.of();
    }

    @Override
    public Optional<com.wolfyscript.utilities.common.gui.Component> getChild(String id) {
        return Optional.empty();
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
        return wolfyUtils.getChat().getMiniMessage().deserialize(staticTitle);
    }

    public String getStaticTitle() {
        return staticTitle;
    }

    @Override
    public int width() {
        return size / height();
    }

    @Override
    public int height() {
        return size / 9;
    }

}
