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

    private final WolfyCoreBukkit core;
    private final PersistentStorage persistentStorage;

    public PersistentStorageListener(WolfyCoreBukkit core) {
        this.core = core;
        this.persistentStorage = core.getPersistentStorage();
    }

    @EventHandler
    private void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        initOrUpdateChunk(chunk);
        //TODO: Start Particle Effects

    }

    @EventHandler
    private void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        ChunkStorage chunkStorage = persistentStorage.getOrCreateWorldStorage(event.getWorld()).getOrCreateChunkStorage(chunk.getX(), chunk.getZ());

        //TODO: Stop Particle Effects


    }

    @EventHandler
    private void onServerLoad(ServerLoadEvent event) {
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                initOrUpdateChunk(chunk);
            }
            world.getForceLoadedChunks().forEach(this::initOrUpdateChunk);
        }
    }

    private void initOrUpdateChunk(Chunk chunk) {
        ChunkStorage chunkStorage = persistentStorage.getOrCreateWorldStorage(chunk.getWorld()).getOrCreateChunkStorage(chunk.getX(), chunk.getZ());
        chunkStorage.loadBlocksIntoCache();
    }


}
