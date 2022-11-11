/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.util.entity;

import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.persistent.player.PlayerParticleEffectData;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@Deprecated
public class PlayerUtils {

    private PlayerUtils() {
    }

    private static Optional<PlayerParticleEffectData> getParticleData(Player player) {
        return WolfyCoreBukkit.getInstance().getPersistentStorage().getOrCreatePlayerStorage(player).getData(PlayerParticleEffectData.class);
    }

    @Deprecated
    public static boolean hasActiveItemEffects(Player player) {
        return getParticleData(player).isPresent();
    }

    @Deprecated
    public static boolean hasActiveItemEffects(Player player, EquipmentSlot equipmentSlot) {
        return getParticleData(player).map(data -> data.hasActiveItemEffects(equipmentSlot)).orElse(false);
    }

    /**
     * Gets the particle effects that are currently active on the player.
     *
     * @param player The player object
     * @return The active particle effects on the player
     */
    @Deprecated
    public static Map<EquipmentSlot, UUID> getActiveItemEffects(Player player) {
        return getParticleData(player).map(PlayerParticleEffectData::getActiveItemEffects).orElseGet(() -> new EnumMap<>(EquipmentSlot.class));
    }

    @Deprecated
    public static UUID getActiveItemEffects(Player player, EquipmentSlot equipmentSlot) {
        return getActiveItemEffects(player).get(equipmentSlot);
    }

    @Deprecated
    public static void setActiveParticleEffect(Player player, EquipmentSlot equipmentSlot, UUID uuid) {
        getParticleData(player).ifPresent(data -> data.setActiveParticleEffect(equipmentSlot, uuid));
    }

    @Deprecated
    public static void stopActiveParticleEffect(Player player, EquipmentSlot equipmentSlot) {
        getParticleData(player).ifPresent(data -> data.stopActiveParticleEffect(equipmentSlot));
    }

    @Deprecated
    public static void loadStores() {
        WolfyUtilities.getWUPlugin().getLogger().info("Loading Player Data");
        if (STORE_FOLDER.exists() || STORE_FOLDER.mkdirs()) {
            String[] files = STORE_FOLDER.list();
            if (files != null) {
                for (String s : files) {
                    if (s.endsWith(".store")) {
                        try {
                            var uuid = UUID.fromString(s.replace(".store", ""));
                            indexedStores.put(uuid, PlayerStore.load(uuid));
                        } catch (IllegalArgumentException e) {
                            WolfyUtilities.getWUPlugin().getLogger().info("Failed to load file " + s + ": " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    public static void saveStores() {
        if (STORE_FOLDER.exists() || STORE_FOLDER.mkdirs()) {
            indexedStores.forEach((uuid, playerStore) -> playerStore.save(uuid));
        }
    }

    @NotNull
    public static PlayerStore getStore(@NotNull Player player) {
        return getStore(player.getUniqueId());
    }

    @NotNull
    public static PlayerStore getStore(@NotNull UUID uuid) {
        indexedStores.computeIfAbsent(uuid, key -> {
            var playerStore = new PlayerStore();
            playerStore.save(key);
            return playerStore;
        });
        return indexedStores.get(uuid);
    }

}
