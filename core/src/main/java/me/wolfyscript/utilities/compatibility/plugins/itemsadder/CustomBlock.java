package me.wolfyscript.utilities.compatibility.plugins.itemsadder;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public interface CustomBlock {

    Optional<CustomBlock> place(Location location);

    boolean remove();

    BlockData generateBlockData();

    Block getBlock();

    boolean isPlaced();

    List<ItemStack> getLoot(boolean includeSelfBlock);

    List<ItemStack> getLoot();

    List<ItemStack> getLoot(ItemStack tool, boolean includeSelfBlock);

    int getOriginalLightLevel();

    void setCurrentLightLevel(int level);
}
