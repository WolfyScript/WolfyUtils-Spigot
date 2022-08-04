package com.wolfyscript.utilities.bukkit.events.persistent;

import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BlockStoreDropItemsEvent extends BlockStoreEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private boolean cancel;
    private final BlockState blockState;
    private final List<Item> items;

    public BlockStoreDropItemsEvent(Player player, Block block, BlockStorage store, BlockState blockState, List<Item> items) {
        super(block, store);
        this.player = player;
        this.blockState = blockState;
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public BlockStorage getStore() {
        return super.getStore();
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
