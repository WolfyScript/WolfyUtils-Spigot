package com.wolfyscript.utilities.bukkit.persistent.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wolfyscript.utilities.KeyedStaticId;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.particles.ParticleUtils;
import org.bukkit.inventory.EquipmentSlot;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@KeyedStaticId(value = "wolfyutilities:particles/effects")
public class PlayerParticleEffectData extends CustomPlayerData {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("particles/effects");

    private final Map<EquipmentSlot, UUID> effectsPerSlot = new EnumMap<>(EquipmentSlot.class);

    public PlayerParticleEffectData() {
        super(ID);
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onUnload() {

    }

    public boolean hasActiveItemEffects(EquipmentSlot equipmentSlot) {
        return effectsPerSlot.containsKey(equipmentSlot);
    }

    /**
     * Gets the particle effects that are currently active on the player.
     *
     * @return The active particle effects on the player
     */
    @JsonIgnore
    public Map<EquipmentSlot, UUID> getActiveItemEffects() {
        return new EnumMap<>(effectsPerSlot);
    }

    public UUID getActiveItemEffects(EquipmentSlot equipmentSlot) {
        return effectsPerSlot.get(equipmentSlot);
    }

    public void setActiveParticleEffect(EquipmentSlot equipmentSlot, UUID effectUUID) {
        stopActiveParticleEffect(equipmentSlot);
        effectsPerSlot.put(equipmentSlot, effectUUID);
    }

    public void stopActiveParticleEffect(EquipmentSlot equipmentSlot) {
        ParticleUtils.stopAnimation(getActiveItemEffects(equipmentSlot));
        effectsPerSlot.remove(equipmentSlot);
    }

    @Override
    public CustomPlayerData copy() {
        return null;
    }
}
