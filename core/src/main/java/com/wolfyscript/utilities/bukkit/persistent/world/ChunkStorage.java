package com.wolfyscript.utilities.bukkit.persistent.world;

import com.wolfyscript.utilities.math.Vec2i;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import me.wolfyscript.utilities.util.particles.ParticleLocation;
import me.wolfyscript.utilities.util.particles.ParticleUtils;
import me.wolfyscript.utilities.util.world.BlockCustomItemStore;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class ChunkStorage {

    private static final NamespacedKey BLOCKS_KEY = new NamespacedKey("wolfyutils", "blocks");

    private static final String BLOCK_POS_KEY = "%s_%s_%s"; //x, y, z
    private static final String BLOCK_POS_NAMESPACE = "wolfyutils"; //-> "wolfyutils:x_y_z"

    private final Map<Vector, BlockCustomItemStore> BLOCKS = new HashMap<>();

    private final Vec2i coords;
    private final WorldStorage worldStorage;

    private ChunkStorage(WorldStorage worldStorage, Vec2i coords) {
        this.coords = coords;
        this.worldStorage = worldStorage;
    }

    public static ChunkStorage create(WorldStorage worldStorage, Vec2i coords) {
        return new ChunkStorage(worldStorage, coords);
    }

    public void loadBlocksIntoCache() {
        getPersistentBlocksContainer().ifPresent(blocks -> {
            blocks.getKeys().forEach(key -> {
                String[] coordsStrings = key.getKey().split("_");
                int[] coords = new int[3];
                for (int i = 0; i < coordsStrings.length; i++) {
                    coords[i] = Integer.parseInt(coordsStrings[i]);
                }
                BLOCKS.put(new Vector(coords[0], coords[1], coords[2]), blocks.get(key, new BlockCustomItemStore.PersistentType()));
            });
        });
    }

    public Optional<Chunk> getChunk() {
        return worldStorage.getWorld().map(world -> world.getChunkAt(coords.getX(), coords.getY()));
    }

    protected Optional<PersistentDataContainer> getPersistentContainer() {
        return worldStorage.getWorld().map(world -> world.getChunkAt(coords.getX(), coords.getY()).getPersistentDataContainer());
    }

    private Optional<PersistentDataContainer> getPersistentBlocksContainer() {
        return getPersistentContainer().map(container -> {
            var context = container.getAdapterContext();
            if (!container.has(BLOCKS_KEY, PersistentDataType.TAG_CONTAINER)) {
                container.set(BLOCKS_KEY, PersistentDataType.TAG_CONTAINER, context.newPersistentDataContainer());
            }
            return container.get(BLOCKS_KEY, PersistentDataType.TAG_CONTAINER);
        });
    }

    /**
     * Stores the BlockCustomItemStore under the specified location.
     *
     * @param location The location to associate the data with.
     * @param blockStore The data of the location.
     */
    public Optional<BlockCustomItemStore> storeBlock(Location location, BlockCustomItemStore blockStore) {
        if (blockStore.getCustomItem().hasNamespacedKey()) {
            var pos = location.toVector();
            var previousStore = BLOCKS.put(pos, blockStore);
            if (previousStore != null) {
                //TODO: Find a more generalised modular system, like running CustomItem actions on removal
                ParticleUtils.stopAnimation(previousStore.getParticleUUID());
            }
            var animation = blockStore.getCustomItem().getParticleContent().getAnimation(ParticleLocation.BLOCK);
            if(animation != null) {
                //TODO: Find a more generalised modular system, like running CustomItem actions on placement
                animation.spawn(location.getBlock());
            }
            updateBlock(pos);
            return Optional.ofNullable(previousStore);
        }
        return Optional.empty();
    }

    /**
     * Removes the stored block at this location and stops every active particle effect.
     *
     * @param location The target location of the block
     * @return Optional of the previously stored data; otherwise empty Optional.
     */
    public Optional<BlockCustomItemStore> removeBlock(Location location) {
        var pos = location.toVector();
        var previousStore = BLOCKS.remove(pos);
        updateBlock(pos);
        if (previousStore != null) {
            //TODO: Find a more generalised modular system, like running CustomItem actions on removal
            ParticleUtils.stopAnimation(previousStore.getParticleUUID());
            return Optional.of(previousStore);
        }
        return Optional.empty();
    }

    /**
     * Gets the stored block at the specified location.
     *
     * @param location The location of the block.
     * @return The stored block if stored; otherwise empty Optional.
     */
    public Optional<BlockCustomItemStore> getBlock(Location location) {
        return Optional.ofNullable(BLOCKS.computeIfAbsent(location.toVector(), pos -> getPersistentBlocksContainer().map(blocks -> blocks.get(createKeyForBlock(pos), new BlockCustomItemStore.PersistentType())).orElse(null)));
    }

    /**
     * Gets the stored blocks in the chunk.
     *
     * @return The stored blocks in the chunk.
     */
    public Map<Vector, BlockCustomItemStore> getStoredBlocks() {
        return BLOCKS.entrySet().stream().filter(entry -> entry.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void updateBlock(Vector blockPos) {
        getPersistentBlocksContainer().ifPresent(blocks -> blocks.set(createKeyForBlock(blockPos), new BlockCustomItemStore.PersistentType(), BLOCKS.get(blockPos)));
    }

    private NamespacedKey createKeyForBlock(Vector blockPos) {
        return new NamespacedKey(BLOCK_POS_NAMESPACE, BLOCK_POS_KEY.formatted(blockPos.getBlockX(), blockPos.getBlockY(), blockPos.getBlockZ()));
    }

    public boolean isBlockStored(Location location) {
        return BLOCKS.containsKey(location.toVector());
    }

    public Optional<BlockCustomItemStore> get(Location location) {
        return Optional.ofNullable(BLOCKS.get(location.toVector()));
    }

}
