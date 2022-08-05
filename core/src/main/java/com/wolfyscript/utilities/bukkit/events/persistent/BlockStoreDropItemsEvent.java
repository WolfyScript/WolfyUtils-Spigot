package com.wolfyscript.utilities.bukkit.events.persistent;

import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockDropItemEvent;
import org.jetbrains.annotations.NotNull;

public class BlockStoreDropItemsEvent extends BlockDropItemEvent implements BlockStoreEvent, Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final BlockStorage blockStorage;

    public BlockStoreDropItemsEvent(@NotNull Block block, @NotNull BlockState blockState, @NotNull BlockStorage blockStorage, @NotNull Player player, @NotNull List<Item> items) {
        super(block, blockState, player, items);
        this.blockStorage = blockStorage;
    }


    @Override
    public BlockStorage getStore() {
        return blockStorage;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
