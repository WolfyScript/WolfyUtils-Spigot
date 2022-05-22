package com.wolfyscript.utilities.bukkit.persistent.world;

import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
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
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WorldStorage {

    private static final NamespacedKey BLOCKS = new NamespacedKey("wolfyutils", "blocks");
    private static final NamespacedKey BLOCK_LOCATION = new NamespacedKey("wolfyutils", "location");
    private static final NamespacedKey BLOCK_DATA = new NamespacedKey("wolfyutils", "data");

    private UUID worldUUID;

    private WorldStorage(World world) {
        this.worldUUID = world.getUID();
    }

    public static WorldStorage wrap(World world) {
        return new WorldStorage(world);
    }

    private Optional<World> getWrappedWorld() {
        return Optional.ofNullable(Bukkit.getWorld(worldUUID));
    }

    public void store(Location location, CustomItem customItem) {
        ParticleUtils.stopAnimation(getStoredEffect(location));
        if (customItem.hasNamespacedKey()) {
            setStore(location, new BlockCustomItemStore(customItem, null));
            var animation = customItem.getParticleContent().getAnimation(ParticleLocation.BLOCK);
            if(animation != null) {
                animation.spawn(location.getBlock());
            }
        }
    }

    /**
     * Removes the stored block at this location and stops every active particle effect.
     *
     * @param location The target location of the block
     */
    public void remove(Location location) {
        ParticleUtils.stopAnimation(getStoredEffect(location));
        store.remove(location);
    }

    public boolean isStored(Location location) {
        return location != null && store.containsKey(location);
    }

    public CustomItem getCustomItem(Location location) {
        BlockCustomItemStore blockStore = get(location);
        return blockStore != null ? blockStore.getCustomItem() : null;
    }

    /**
     * The current active particle effect on this Location.
     *
     * @param location The location to be checked.
     * @return The uuid of the currently active particle effect.
     */
    @Nullable
    public UUID getStoredEffect(@Nullable Location location) {
        BlockCustomItemStore blockStore = get(location);
        return blockStore != null ? blockStore.getParticleUUID() : null;
    }

    public boolean hasStoredEffect(Location location) {
        return isStored(location) && getStoredEffect(location) != null;
    }

    void setStore(Location location, BlockCustomItemStore blockStore) {
        store.put(location, blockStore);
    }

    public Optional<BlockCustomItemStore> get(Location location) {
        return Optional.ofNullable(getCustomBlockStores().get(location));
    }

    private Optional<PersistentDataContainer> getWorldContainer() {
        return getWrappedWorld().map(PersistentDataHolder::getPersistentDataContainer);
    }

    private Optional<PersistentDataContainer> getCustomStore() {
        return getWorldContainer().map(container -> container.getOrDefault(BLOCK_DATA, PersistentDataType.TAG_CONTAINER, container.getAdapterContext().newPersistentDataContainer()));
    }

    private Optional<PersistentDataContainer[]> getBlocks() {
        return getCustomStore().map(container -> container.getOrDefault(BLOCKS, PersistentDataType.TAG_CONTAINER_ARRAY, new PersistentDataContainer[0]));
    }

    private void setBlocks(PersistentDataContainer[] blockDataArray) {
        getCustomStore().ifPresent(container -> container.set(BLOCKS, PersistentDataType.TAG_CONTAINER_ARRAY, blockDataArray));
    }

    private Map<Location, BlockCustomItemStore> getCustomBlockStores() {
        return getBlocks().map(dataArray -> {
            Map<Location, BlockCustomItemStore> blockData = new HashMap<>();
            for (PersistentDataContainer dataContainer : dataArray) {
                LocationConverter.read(getWrappedWorld().get(), BLOCK_LOCATION, dataContainer).ifPresent(location -> {
                    BlockCustomItemStore.read(BLOCK_DATA, dataContainer).ifPresent(blockCustomItemStore -> blockData.put(location, blockCustomItemStore));
                });
            }
            return blockData;
        }).orElseGet(HashMap::new);
    }

    public void setCustomBlockStores(Map<Location, BlockCustomItemStore> blockData) {
        getCustomStore().ifPresent(container -> {
            var context = container.getAdapterContext();
            PersistentDataContainer[] dataArray = new PersistentDataContainer[blockData.size()];
            int index = 0;
            for (var entry : blockData.entrySet()) {
                var data = context.newPersistentDataContainer();
                var loc = entry.getKey();
                LocationConverter.write(loc, BLOCK_LOCATION, container);
                entry.getValue().write(BLOCK_DATA, data);
                dataArray[index] = data;
                index++;
            }
            container.set(new NamespacedKey("wolfyutils", "blocks"), PersistentDataType.TAG_CONTAINER_ARRAY, dataArray);
        });

    }

}
