package com.wolfyscript.utilities.bukkit.events.persistent;

import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BlockStorageMultiPlaceEvent extends BlockMultiPlaceEvent implements BlockStorageEvent {

    private final BlockStorage blockStorage;
    private final List<BlockStorage> blockStorages;

    public BlockStorageMultiPlaceEvent(@NotNull List<BlockState> states, List<BlockStorage> blockStorages, @NotNull Block clicked, @NotNull ItemStack itemInHand, @NotNull Player thePlayer, boolean canBuild) {
        super(states, clicked, itemInHand, thePlayer, canBuild);
        this.blockStorage = blockStorages.get(0);
        this.blockStorages = blockStorages;
    }

    public List<BlockStorage> getBlockStorages() {
        return blockStorages;
    }

    @Override
    public BlockStorage getStorage() {
        return blockStorage;
    }
}
