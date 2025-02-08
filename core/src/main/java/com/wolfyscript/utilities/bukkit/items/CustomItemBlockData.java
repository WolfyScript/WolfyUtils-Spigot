package com.wolfyscript.utilities.bukkit.items;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.bukkit.events.persistent.BlockStorageBreakEvent;
import com.wolfyscript.utilities.bukkit.events.persistent.BlockStoragePlaceEvent;
import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import com.wolfyscript.utilities.bukkit.persistent.world.ChunkStorage;
import com.wolfyscript.utilities.bukkit.persistent.world.CustomBlockData;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.events.CustomItemBreakEvent;
import me.wolfyscript.utilities.util.particles.ParticleLocation;
import me.wolfyscript.utilities.util.particles.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class CustomItemBlockData extends CustomBlockData {

    public static final NamespacedKey ID = new NamespacedKey(NamespacedKey.WOLFYUTILITIES, "custom_item");

    @JsonIgnore
    private final WolfyUtilCore core;
    @JsonIgnore
    private final ChunkStorage chunkStorage;
    @JsonIgnore
    private final Vector pos;
    @JsonIgnore
    private UUID particleAnimationID;

    private final NamespacedKey item;

    @JsonCreator
    public CustomItemBlockData(@JacksonInject WolfyUtilCore core, @JacksonInject ChunkStorage chunkStorage, @JacksonInject Vector pos, @JsonProperty("item") NamespacedKey item) {
        super(ID);
        this.core = core;
        this.chunkStorage = chunkStorage;
        this.pos = pos;
        this.item = item;
        this.particleAnimationID = null;
    }

    private CustomItemBlockData(CustomItemBlockData other) {
        super(ID);
        this.core = other.core;
        this.chunkStorage = other.chunkStorage;
        this.pos = other.pos;
        this.item = new NamespacedKey(other.getNamespacedKey().getNamespace(), other.getNamespacedKey().getKey());
        this.particleAnimationID = null;
    }

    public NamespacedKey getItem() {
        return item;
    }

    @JsonIgnore
    public Optional<CustomItem> getCustomItem() {
        return Optional.ofNullable(core.getRegistries().getCustomItems().get(getItem()));
    }

    @JsonIgnore
    public Optional<UUID> getAnimation() {
        return Optional.of(particleAnimationID);
    }

    public void setParticleAnimationID(@Nullable UUID particleAnimationID) {
        this.particleAnimationID = particleAnimationID;
    }

    public void onPlace(BlockStoragePlaceEvent event) {
        getCustomItem().ifPresent(customItem -> {
            var animation = customItem.getParticleContent().getAnimation(ParticleLocation.BLOCK);
            if (animation != null) {
                setParticleAnimationID(animation.spawn(event.getBlockPlaced()));
            }
        });
    }

    public void onBreak(BlockStorageBreakEvent event) {
        getCustomItem().ifPresent(customItem -> {
            var event1 = new CustomItemBreakEvent(customItem, event);
            Bukkit.getPluginManager().callEvent(event1);
            event.setCancelled(event1.isCancelled());
        });
        ParticleUtils.stopAnimation(particleAnimationID);
    }

    @Override
    public void onLoad() {
        getCustomItem().ifPresent(customItem -> {
            var animation = customItem.getParticleContent().getAnimation(ParticleLocation.BLOCK);
            if (animation != null) {
                chunkStorage.getChunk().ifPresent(chunk -> setParticleAnimationID(animation.spawn(chunk.getWorld().getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()))));
            }
        });
    }

    @Override
    public void onUnload() {
        ParticleUtils.stopAnimation(particleAnimationID);
    }

    @Override
    public CustomItemBlockData copy() {
        return new CustomItemBlockData(this);
    }

    @Override
    public CustomItemBlockData copyTo(BlockStorage storage) {
        return new CustomItemBlockData(core, storage.getChunkStorage(), storage.getPos(), item);
    }
}
