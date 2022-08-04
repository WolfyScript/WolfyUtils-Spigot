package com.wolfyscript.utilities.bukkit.persistent.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Optional;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public abstract class CustomBlockData implements Keyed {

    private final NamespacedKey id;

    protected CustomBlockData(NamespacedKey id) {
        this.id = id;
    }

    public abstract void onPlace(BlockPlaceEvent event);

    public abstract void onBreak(BlockBreakEvent event);

    public Optional<List<ItemStack>> dropItems(Location location) {
        return Optional.empty();
    }

    public void onUnload() {

    }

    public abstract CustomBlockData copy();

    @JsonIgnore
    @Override
    public NamespacedKey getNamespacedKey() {
        return id;
    }
}
