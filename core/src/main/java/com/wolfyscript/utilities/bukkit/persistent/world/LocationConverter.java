package com.wolfyscript.utilities.bukkit.persistent.world;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class LocationConverter {

    public static Optional<Location> read(World world, NamespacedKey key, PersistentDataContainer container) {
        int[] data = container.getOrDefault(key, PersistentDataType.INTEGER_ARRAY, new int[0]);
        return data.length == 3 ? Optional.of(new Location(world, data[0], data[1], data[2])) : Optional.empty();
    }

    public static PersistentDataContainer write(Location location, NamespacedKey key, PersistentDataContainer container) {
        container.set(key, PersistentDataType.INTEGER_ARRAY, new int[] {location.getBlockX(), location.getBlockY(), location.getBlockZ()});
        return container;
    }

}
