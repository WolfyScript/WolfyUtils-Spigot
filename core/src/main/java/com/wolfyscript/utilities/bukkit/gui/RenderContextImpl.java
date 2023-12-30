package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.platform.adapters.ItemStack;
import com.wolfyscript.utilities.bukkit.WolfyCoreImpl;
import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl;
import com.wolfyscript.utilities.bukkit.adapters.PlayerImpl;
import com.wolfyscript.utilities.bukkit.eval.context.EvalContextPlayer;
import com.wolfyscript.utilities.bukkit.nms.inventory.InventoryUpdate;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.eval.context.EvalContext;
import com.wolfyscript.utilities.gui.*;
import com.wolfyscript.utilities.versioning.MinecraftVersion;
import com.wolfyscript.utilities.versioning.ServerVersion;
import com.wolfyscript.utilities.world.items.ItemStackConfig;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class RenderContextImpl implements RenderContext {

    private final GuiHolder holder;
    private final Inventory inventory;
    private final Window window;
    private final Router router;
    private Component currentNode;
    private int slotOffsetToParent;

    public RenderContextImpl(GuiHolder holder, Inventory inventory, Router router, Window window) {
        this.holder = holder;
        this.inventory = inventory;
        this.router = router;
        this.window = window;
        this.slotOffsetToParent = 0;
        this.currentNode = null;
    }

    @Override
    public GuiHolder holder() {
        return holder;
    }

    public void setSlotOffset(int offset) {
        this.slotOffsetToParent = offset;
    }

    @Override
    public int currentOffset() {
        return slotOffsetToParent;
    }

    @Override
    public void enterNode(Component component) {
        this.currentNode = component;
        this.slotOffsetToParent = component.offset();
    }

    @Override
    public void exitNode() {
        this.currentNode = null;
    }

    Inventory getInventory() {
        return inventory;
    }

    @Override
    public Component getCurrentComponent() {
        return currentNode;
    }

    @Override
    public void setStack(int i, ItemStackConfig itemStackConfig) {
        if (itemStackConfig == null) {
            inventory.setItem(i, null);
            return;
        }
        if (!(itemStackConfig instanceof BukkitItemStackConfig bukkitItemStackConfig))
            throw new IllegalArgumentException(String.format("Cannot render stack config! Invalid stack config type! Expected '%s' but received '%s'.", BukkitItemStackConfig.class.getName(), itemStackConfig.getClass().getName()));

        inventory.setItem(i, bukkitItemStackConfig.constructItemStack().getBukkitRef());
    }

    @Override
    public void renderStack(Position position, ItemStack itemStack) {
        if (itemStack == null) {
            setNativeStack(currentOffset() + position.slot(), null);
            return;
        }
        if (!(itemStack instanceof ItemStackImpl stack))
            throw new IllegalArgumentException(String.format("Cannot render stack! Invalid stack config type! Expected '%s' but received '%s'.", ItemStackImpl.class.getName(), itemStack.getClass().getName()));

        setNativeStack(currentOffset() + position.slot(), stack.getBukkitRef());
    }

    @Override
    public void renderStack(Position position, ItemStackConfig itemStackConfig, ItemStackContext itemStackContext) {
        if (!(itemStackConfig instanceof BukkitItemStackConfig bukkitItemStackConfig))
            throw new IllegalArgumentException(String.format("Cannot render stack config! Invalid stack config type! Expected '%s' but received '%s'.", BukkitItemStackConfig.class.getName(), itemStackConfig.getClass().getName()));

        setNativeStack(
                currentOffset() + position.slot(),
                bukkitItemStackConfig.constructItemStack(null, router.getWolfyUtils().getChat().getMiniMessage(), itemStackContext.resolvers()).getBukkitRef()
        );
    }

    private void setNativeStack(int i, org.bukkit.inventory.ItemStack itemStack) {
        //checkIfSlotInBounds(i);
        if (itemStack == null) {
            inventory.setItem(i, null);
            return;
        }
        inventory.setItem(i, itemStack);
    }

    @Override
    public void updateTitle(GuiHolder holder, net.kyori.adventure.text.Component component) {
        Player player = ((PlayerImpl) holder.getPlayer()).getBukkitRef();
        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0))) {
            player.getOpenInventory().setTitle(BukkitComponentSerializer.legacy().serialize(component));
        } else {
            InventoryUpdate.updateInventory(
                    ((WolfyCoreImpl) window.getWolfyUtils().getCore()).getWolfyUtils().getPlugin(),
                    player,
                    component
            );
        }
    }

    @Override
    public ItemStackContext createContext(GuiHolder guiHolder, TagResolver tagResolvers) {
        return new ItemStackContext() {
            @Override
            public TagResolver resolvers() {
                return tagResolvers;
            }

            @Override
            public MiniMessage miniMessage() {
                return window.getWolfyUtils().getChat().getMiniMessage();
            }

            @Override
            public EvalContext evalContext() {
                return new EvalContextPlayer(((PlayerImpl) guiHolder.getPlayer()).getBukkitRef());
            }

            @Override
            public GuiHolder holder() {
                return guiHolder;
            }
        };
    }

    @Override
    public void openAndRenderMenuFor(GuiViewManager viewManager, UUID viewer) {
        Player player = Bukkit.getPlayer(viewer);
        if (player.getOpenInventory().getTopInventory() != inventory) {
            player.openInventory(inventory);
            viewManager.getCurrentMenu().ifPresent(window -> {
                GuiHolder holder = ((BukkitInventoryGuiHolder) getInventory().getHolder()).guiHolder();
                var dynamic = window.construct(holder, viewManager);
                dynamic.open(viewManager);
                dynamic.render(holder, viewManager, this);
                ((GuiViewManagerImpl) viewManager).setCurrentRoot(dynamic);
            });
        }
        viewManager.updateSignalQueue(this);
    }

}
