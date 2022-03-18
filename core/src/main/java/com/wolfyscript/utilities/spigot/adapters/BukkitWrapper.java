package com.wolfyscript.utilities.spigot.adapters;

import com.wolfyscript.utilities.common.adapters.Entity;
import com.wolfyscript.utilities.common.adapters.Location;
import com.wolfyscript.utilities.common.adapters.World;

public class BukkitWrapper {

    public static Location adapt(org.bukkit.Location bukkitLoc) {
        return new LocationImpl(bukkitLoc);
    }

    public static Entity adapt(org.bukkit.entity.Entity bukkitEntity) {
        return new EntityImpl(bukkitEntity);
    }

    public static World adapt(org.bukkit.World world) {
        return new WorldImpl(world);
    }
}
