package com.wolfyscript.utilities.spigot.adapters;

import com.wolfyscript.utilities.common.adapters.Location;
import com.wolfyscript.utilities.common.adapters.Vector3D;
import com.wolfyscript.utilities.common.adapters.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class EntityImpl extends BukkitRefAdapter<Entity> implements com.wolfyscript.utilities.common.adapters.Entity {

    public EntityImpl(Entity entity) {
        super(entity);
    }


    @Override
    public @NotNull Location getLocation() {
        return BukkitWrapper.adapt(bukkitRef.getLocation());
    }

    @Override
    public @Nullable Location getLocation(Location location) {
        return BukkitWrapper.adapt(bukkitRef.getLocation(((LocationImpl) location).getBukkitRef()));
    }

    @Override
    public void setVelocity(@NotNull Vector3D vector3D) {

    }

    @Override
    public @NotNull Vector3D getVelocity() {
        return null;
    }

    @Override
    public double getHeight() {
        return bukkitRef.getHeight();
    }

    @Override
    public double getWidth() {
        return bukkitRef.getWidth();
    }

    @Override
    public boolean isOnGround() {
        return bukkitRef.isOnGround();
    }

    @Override
    public boolean isInWater() {
        return bukkitRef.isInWater();
    }

    @Override
    public @NotNull World getWorld() {
        return BukkitWrapper.adapt(bukkitRef.getWorld());
    }

    @Override
    public void setRotation(float v, float v1) {

    }
}
