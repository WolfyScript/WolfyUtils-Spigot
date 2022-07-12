package com.wolfyscript.utilities.bukkit.persistent.world;

import com.wolfyscript.utilities.bukkit.persistent.PersistentStorage;
import me.wolfyscript.utilities.util.world.BlockCustomItemStore;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;

public class BlockStorage {

    private static final NamespacedKey LOCATION_KEY = new NamespacedKey("wolfyutils", "location");
    private static final NamespacedKey DATA_KEY = new NamespacedKey("wolfyutils", "data");

    private final Vector pos;
    private PersistentDataContainer container;

    public BlockStorage(Vector pos) {
        this.pos = pos;
    }

    public BlockCustomItemStore store(BlockCustomItemStore customItemStore) {

        return null; //TODO: Return previous stored value
    }


}
