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

    private static final NamespacedKey DATA = new NamespacedKey("wolfyutils", "data");
    private static final NamespacedKey BLOCKS = new NamespacedKey("wolfyutils", "blocks");
    private static final NamespacedKey BLOCK_LOCATION = new NamespacedKey("wolfyutils", "location");
    private static final NamespacedKey BLOCK_DATA = new NamespacedKey("wolfyutils", "data");

    private final Map<Vec2i, ChunkStorage> CHUNK_DATA = new HashMap<>();

    private Map<Location, BlockCustomItemStore> BLOCKS_MAP = new HashMap<>();
    private final UUID worldUUID;

    public WorldStorage(UUID world) {
        this.worldUUID = world;
    }

    private Optional<World> getWorld() {
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
            ChunkStorage chunkStorage = CHUNK_DATA.computeIfAbsent(new Vec2i(location.getBlockX() >> 4, location.getBlockZ() >> 4), vec2i -> new ChunkStorage(vec2i));



            var previousStore = BLOCKS_MAP.put(location, blockStore);
            if (previousStore != null) {
                ParticleUtils.stopAnimation(previousStore.getParticleUUID());
            }
            var animation = blockStore.getCustomItem().getParticleContent().getAnimation(ParticleLocation.BLOCK);
            if(animation != null) {
                animation.spawn(location.getBlock());
            }
            saveBlocksStore();
        }
    }

    /**
     * Removes the stored block at this location and stops every active particle effect.
     *
     * @param location The target location of the block
     */
    public void removeBlock(Location location) {
        var previousStore = BLOCKS_MAP.remove(location);
        if (previousStore != null) {
            ParticleUtils.stopAnimation(previousStore.getParticleUUID());
        }
        saveBlocksStore();
    }

    public boolean isBlockStored(Location location) {
        return BLOCKS_MAP.containsKey(location);
    }

    public Optional<BlockCustomItemStore> get(Location location) {
        return Optional.ofNullable(BLOCKS_MAP.get(location));
    }

    private Optional<PersistentDataContainer> getWorldContainer() {
        return getWorld().map(PersistentDataHolder::getPersistentDataContainer);
    }

    /**
     * Loads the state from the persistent storage.
     */
    public void loadBlocksStore() {
        this.BLOCKS_MAP = getWorldContainer().map(worldContainer -> {
            var context = worldContainer.getAdapterContext();
            var customStore = worldContainer.getOrDefault(DATA, PersistentDataType.TAG_CONTAINER, context.newPersistentDataContainer());
            var dataArray = customStore.getOrDefault(BLOCKS, PersistentDataType.TAG_CONTAINER_ARRAY, new PersistentDataContainer[0]);
            Map<Location, BlockCustomItemStore> blockData = new HashMap<>();
            for (PersistentDataContainer dataContainer : dataArray) {
                Location location = dataContainer.get(BLOCK_LOCATION, new LocationConverter(getWorld().get()));
                BlockCustomItemStore store = dataContainer.getOrDefault(BLOCK_DATA, new BlockCustomItemStore.PersistentType(), new BlockCustomItemStore((me.wolfyscript.utilities.util.NamespacedKey) null, null));
                if (store.getCustomItem() != null) {
                    blockData.put(location, store);
                }
            }
            return blockData;
        }).orElseGet(HashMap::new);
    }

    /**
     * Saves the current state of the store to the persistent storage.
     */
    public void saveBlocksStore() {
        getWorldContainer().ifPresent(worldContainer -> {
            var context = worldContainer.getAdapterContext();
            var customStore = worldContainer.getOrDefault(DATA, PersistentDataType.TAG_CONTAINER, context.newPersistentDataContainer());
            PersistentDataContainer[] dataArray = new PersistentDataContainer[BLOCKS_MAP.size()];
            int index = 0;
            for (var entry : BLOCKS_MAP.entrySet()) {
                var data = context.newPersistentDataContainer();
                data.set(BLOCK_LOCATION, new LocationConverter(null), entry.getKey());
                data.set(BLOCK_DATA, new BlockCustomItemStore.PersistentType(), entry.getValue());
                dataArray[index] = data;
                index++;
            }
            customStore.set(BLOCKS, PersistentDataType.TAG_CONTAINER_ARRAY, dataArray);
            worldContainer.set(DATA, PersistentDataType.TAG_CONTAINER, customStore);
        });
    }

}
