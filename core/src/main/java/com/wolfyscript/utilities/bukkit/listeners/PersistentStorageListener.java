package com.wolfyscript.utilities.bukkit.listeners;

import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.persistent.PersistentStorage;
import com.wolfyscript.utilities.bukkit.persistent.world.ChunkStorage;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        ChunkStorage chunkStorage = persistentStorage.getOrCreateWorldStorage(event.getWorld()).getOrCreateChunkStorage(chunk.getX(), chunk.getZ());


        //TODO: Start Particle Effects

    }

    @EventHandler
    private void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        ChunkStorage chunkStorage = persistentStorage.getOrCreateWorldStorage(event.getWorld()).getOrCreateChunkStorage(chunk.getX(), chunk.getZ());

        //TODO: Stop Particle Effects


    }


}
