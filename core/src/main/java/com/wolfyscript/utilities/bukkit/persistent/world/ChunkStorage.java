package com.wolfyscript.utilities.bukkit.persistent.world;

import com.wolfyscript.utilities.math.Vec2i;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import me.wolfyscript.utilities.util.particles.ParticleLocation;
import me.wolfyscript.utilities.util.particles.ParticleUtils;
import me.wolfyscript.utilities.util.world.BlockCustomItemStore;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.util.Vector;

public class ChunkStorage {

    private static final NamespacedKey BLOCKS_KEY = new NamespacedKey("wolfyutils", "blocks");

    private final Map<Vector, BlockCustomItemStore> BLOCKS = new HashMap<>();

    private final Vec2i coords;

    public ChunkStorage(Vec2i coords) {
        this.coords = coords;
    }

    /**
     * Stores the BlockCustomItemStore under the specified location.
     *
     * @param location The location to associate the data with.
     * @param blockStore The data of the location.
     */
    public void storeBlock(Location location, BlockCustomItemStore blockStore) {
        if (blockStore.getCustomItem().hasNamespacedKey()) {
            var previousStore = BLOCKS.put(location.toVector(), blockStore);
            if (previousStore != null) {
                ParticleUtils.stopAnimation(previousStore.getParticleUUID());
            }
            var animation = blockStore.getCustomItem().getParticleContent().getAnimation(ParticleLocation.BLOCK);
            if(animation != null) {
                animation.spawn(location.getBlock());
            }
            //TODO: Update persistent container to reflect changes
        }
    }

    /**
     * Removes the stored block at this location and stops every active particle effect.
     *
     * @param location The target location of the block
     */
    public void removeBlock(Location location) {
        var previousStore = BLOCKS.remove(location.toVector());
        if (previousStore != null) {
            ParticleUtils.stopAnimation(previousStore.getParticleUUID());
        }
        //TODO: Update persistent container to reflect changes
    }

    public boolean isBlockStored(Location location) {
        return BLOCKS.containsKey(location.toVector());
    }

    public Optional<BlockCustomItemStore> get(Location location) {
        return Optional.ofNullable(BLOCKS.get(location.toVector()));
    }

}
