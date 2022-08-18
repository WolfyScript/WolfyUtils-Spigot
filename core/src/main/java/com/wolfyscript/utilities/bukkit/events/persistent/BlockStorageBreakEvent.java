package com.wolfyscript.utilities.bukkit.events.persistent;

import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class BlockStorageBreakEvent extends BlockBreakEvent implements BlockStorageEvent {

    private static final HandlerList handlers = new HandlerList();

    protected BlockStorage blockStorage;

    public BlockStorageBreakEvent(@NotNull Block theBlock, BlockStorage blockStorage, @NotNull Player player) {
        super(theBlock, player);
        this.blockStorage = blockStorage;
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
    public BlockStorage getStorage() {
        return blockStorage;
    }
}
