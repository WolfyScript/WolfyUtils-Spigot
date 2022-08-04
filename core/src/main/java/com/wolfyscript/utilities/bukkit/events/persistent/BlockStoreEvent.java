package com.wolfyscript.utilities.bukkit.events.persistent;

import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import org.bukkit.block.Block;
import org.bukkit.event.Event;

public abstract class BlockStoreEvent extends Event {

    protected Block block;
    protected BlockStorage store;

    protected BlockStoreEvent(Block block, BlockStorage store) {
        this.block = block;
        this.store = store;
    }

    public Block getBlock() {
        return block;
    }

    public BlockStorage getStore() {
        return store;
    }
}
