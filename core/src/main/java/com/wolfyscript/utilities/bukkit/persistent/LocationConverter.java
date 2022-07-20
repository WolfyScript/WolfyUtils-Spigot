package com.wolfyscript.utilities.bukkit.persistent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LocationConverter implements PersistentDataType<int[], Location> {

    private final UUID world;

    public LocationConverter(World world) {
        this.world = world == null ? null : world.getUID();
    }

    @NotNull
    @Override
    public Class<int[]> getPrimitiveType() {
        return int[].class;
    }

    @NotNull
    @Override
    public Class<Location> getComplexType() {
        return Location.class;
    }

    @Override
    public int @NotNull [] toPrimitive(@NotNull Location location, @NotNull PersistentDataAdapterContext context) {
        return new int[] { location.getBlockX(), location.getBlockY(), location.getBlockZ() };
    }

    @NotNull
    @Override
    public Location fromPrimitive(int @NotNull [] data, @NotNull PersistentDataAdapterContext context) {
        return data.length == 3 ? new Location(Bukkit.getWorld(world), data[0], data[1], data[2]) : new Location(Bukkit.getWorld(world), 0, 0, 0);
    }
}
