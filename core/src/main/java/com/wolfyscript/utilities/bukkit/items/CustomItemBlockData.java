package com.wolfyscript.utilities.bukkit.items;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.bukkit.persistent.world.CustomBlockData;
import java.util.Optional;
import java.util.UUID;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.events.CustomItemBreakEvent;
import me.wolfyscript.utilities.util.particles.ParticleLocation;
import me.wolfyscript.utilities.util.particles.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.Nullable;

public class CustomItemBlockData extends CustomBlockData {

    public static final NamespacedKey ID = new NamespacedKey(NamespacedKey.WOLFYUTILITIES, "custom_item");

    @JsonIgnore
    private final WolfyUtilCore core;
    @JsonIgnore
    private UUID particleAnimationID;

    private final NamespacedKey item;

    @JsonCreator
    public CustomItemBlockData(@JacksonInject("core") WolfyUtilCore core, @JsonProperty("item") NamespacedKey item) {
        super(ID);
        this.core = core;
        this.item = item;
        this.particleAnimationID = null;
    }

    private CustomItemBlockData(CustomItemBlockData other) {
        super(ID);
        this.core = other.core;
        this.item = NamespacedKey.of(other.getNamespacedKey().toString());
        this.particleAnimationID = null;
    }

    public NamespacedKey getItem() {
        return item;
    }

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
                animation.spawn(event.getBlock());
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
    public void onUnload() {
        ParticleUtils.stopAnimation(particleAnimationID);
    }

    @Override
    public CustomItemBlockData copy() {
        return new CustomItemBlockData(this);
    }
}
