package com.wolfyscript.utilities.bukkit.persistent;

import com.wolfyscript.utilities.bukkit.persistent.world.WorldStorage;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PersistentStorage {

    private final Map<UUID, WorldStorage> WORLD_STORAGE = new HashMap<>();

    public PersistentStorage() {

    }

    public WorldStorage getOrCreateWorldStorage(World world) {
        return WORLD_STORAGE.computeIfAbsent(world.getUID(), WorldStorage::new);
    }

}
