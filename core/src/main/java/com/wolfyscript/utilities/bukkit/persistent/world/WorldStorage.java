package com.wolfyscript.utilities.bukkit.persistent.world;

import com.wolfyscript.utilities.math.Vec2i;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.util.world.BlockCustomItemStore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WorldStorage {

    private final Map<Vec2i, ChunkStorage> CHUNK_DATA = new HashMap<>();

    private final WolfyUtilCore core;
    private final UUID worldUUID;

    public WorldStorage(WolfyUtilCore core, UUID world) {
        this.worldUUID = world;
        this.core = core;
    }

    public WolfyUtilCore getCore() {
        return core;
    }

    protected Optional<World> getWorld() {
        return Optional.ofNullable(Bukkit.getWorld(worldUUID));
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

    public BlockStorage getOrCreateBlockStorage(Location location) {
        return getOrCreateChunkStorage(location).getOrCreateBlockStorage(location);
    }

    /**
     * Removes the stored block at this location and stops every active particle effect.
     *
     * @param location The target location of the block
     */
    public Optional<BlockStorage> removeBlock(Location location) {
        return getOrCreateChunkStorage(location).removeBlock(location);
    }

    public boolean isBlockStored(Location location) {
        return getBlock(location).isPresent();
    }

    public Optional<BlockStorage> getBlock(Location location) {
        return getOrCreateChunkStorage(location).getBlock(location);
    }

    protected Optional<PersistentDataContainer> getWorldContainer() {
        return getWorld().map(PersistentDataHolder::getPersistentDataContainer);
    }

}
