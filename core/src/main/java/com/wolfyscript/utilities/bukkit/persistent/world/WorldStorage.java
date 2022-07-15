package com.wolfyscript.utilities.bukkit.persistent.world;

import com.wolfyscript.utilities.bukkit.persistent.LocationConverter;
import com.wolfyscript.utilities.math.Vec2i;
import de.tr7zw.changeme.nbtapi.NBTChunk;
import me.wolfyscript.utilities.util.particles.ParticleLocation;
import me.wolfyscript.utilities.util.particles.ParticleUtils;
import me.wolfyscript.utilities.util.world.BlockCustomItemStore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WorldStorage {

    private final Map<Vec2i, ChunkStorage> CHUNK_DATA = new HashMap<>();

    private final UUID worldUUID;

    public WorldStorage(UUID world) {
        this.worldUUID = world;
    }

    protected Optional<World> getWorld() {
        return Optional.ofNullable(Bukkit.getWorld(worldUUID));
    }

    /**
     * Stores the BlockCustomItemStore under the specified location.
     *
     * @param location The location to associate the data with.
     * @param blockStore The data of the location.
     */
    public void storeBlock(Location location, BlockCustomItemStore blockStore) {
        if (blockStore.getCustomItem().hasNamespacedKey()) {
            getOrCreateChunkStorage(location).storeBlock(location, blockStore);
        }
    }

    public ChunkStorage getOrCreateChunkStorage(Vec2i chunkCoords) {
        return CHUNK_DATA.computeIfAbsent(chunkCoords, vec2i -> ChunkStorage.create(this, vec2i));
    }

    public ChunkStorage getOrCreateChunkStorage(int chunkX, int chunkZ) {
        return getOrCreateChunkStorage(new Vec2i(chunkX, chunkZ));
    }

    public ChunkStorage getOrCreateChunkStorage(Location location) {
        return getOrCreateChunkStorage(new Vec2i(location.getBlockX() >> 4, location.getBlockZ() >> 4));
    }

    /**
     * Removes the stored block at this location and stops every active particle effect.
     *
     * @param location The target location of the block
     */
    public void removeBlock(Location location) {
        getOrCreateChunkStorage(location).removeBlock(location);
    }

    public boolean isBlockStored(Location location) {
        return get(location).isPresent();
    }

    public Optional<BlockCustomItemStore> get(Location location) {
        return getOrCreateChunkStorage(location).getBlock(location);
    }

    protected Optional<PersistentDataContainer> getWorldContainer() {
        return getWorld().map(PersistentDataHolder::getPersistentDataContainer);
    }

}
