package com.wolfyscript.utilities.bukkit.items;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilCore;
import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import com.wolfyscript.utilities.bukkit.persistent.world.ChunkStorage;
import com.wolfyscript.utilities.bukkit.persistent.world.CustomBlockData;
import java.util.Optional;
import java.util.UUID;
import com.wolfyscript.utilities.bukkit.events.CustomItemBreakEvent;
import com.wolfyscript.utilities.bukkit.particles.ParticleLocation;
import com.wolfyscript.utilities.bukkit.particles.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class CustomItemBlockData extends CustomBlockData {

    public static final NamespacedKey ID = new BukkitNamespacedKey(BukkitNamespacedKey.WOLFYUTILITIES, "custom_item");

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
        this.item = new BukkitNamespacedKey(other.getNamespacedKey().getNamespace(), other.getNamespacedKey().getKey());
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

    public void onPlace(BlockPlaceEvent event) {
        getCustomItem().ifPresent(customItem -> {
            var animation = customItem.getParticleContent().getAnimation(ParticleLocation.BLOCK);
            if (animation != null) {
                setParticleAnimationID(animation.spawn(event.getBlockPlaced()));
            }
        });
    }

    public void onBreak(BlockBreakEvent event) {
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
