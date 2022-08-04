package com.wolfyscript.utilities.bukkit.listeners;

import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.persistent.PersistentStorage;
import com.wolfyscript.utilities.bukkit.persistent.world.ChunkStorage;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class PersistentStorageListener implements Listener {

    private static final String PREVIOUS_BROKEN_STORE = "previous_store";

    private final WolfyCoreBukkit core;
    private final PersistentStorage persistentStorage;

    public PersistentStorageListener(WolfyCoreBukkit core) {
        this.core = core;
        this.persistentStorage = core.getPersistentStorage();
    }

    @EventHandler
    private void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        var chunkStorage = initOrUpdateChunk(chunk);
        startParticles(chunkStorage);
    }

    @EventHandler
    private void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        ChunkStorage chunkStorage = persistentStorage.getOrCreateWorldStorage(event.getWorld()).getOrCreateChunkStorage(chunk.getX(), chunk.getZ());

        //TODO: Find a more modular system to stop particles, like running CustomItem actions on unload
        chunkStorage.getStoredBlocks().forEach((vector, store) -> {
            //TODO: onUnLoad
        });
    }

    @EventHandler
    private void onServerLoad(ServerLoadEvent event) {
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                initOrUpdateChunk(chunk);
            }
        }
    }

    private void startParticles(ChunkStorage chunkStorage) {
        //TODO: Find a more generalised modular system, like running CustomItem actions on load
        chunkStorage.getChunk().ifPresent(chunk -> {
            chunkStorage.getStoredBlocks().forEach((vector, blockStore) -> {

                /*
                var animation = blockStore.getCustomItem().getParticleContent().getAnimation(ParticleLocation.BLOCK);
                if(animation != null) {
                    animation.spawn(new Location(chunk.getWorld(), vector.getX(),vector.getY(),vector.getZ()).getBlock());
                }
                 */
            });
        });
    }

    private ChunkStorage initOrUpdateChunk(Chunk chunk) {
        ChunkStorage chunkStorage = persistentStorage.getOrCreateWorldStorage(chunk.getWorld()).getOrCreateChunkStorage(chunk.getX(), chunk.getZ());
        chunkStorage.loadBlocksIntoCache();
        return chunkStorage;
    }


}
