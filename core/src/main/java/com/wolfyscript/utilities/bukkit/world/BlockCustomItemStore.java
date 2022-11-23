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

package com.wolfyscript.utilities.bukkit.world;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilCore;
import com.wolfyscript.utilities.bukkit.items.CustomItem;
import java.io.IOException;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Replaced by {@link com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage}, that is available
 * via the {@link com.wolfyscript.utilities.bukkit.persistent.world.ChunkStorage} methods.<br>
 * For convenience {@link  com.wolfyscript.utilities.bukkit.persistent.world.WorldStorage} methods can be used too.
 * @deprecated Replaced by {@link com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage}
 * @see com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage
 */
@JsonDeserialize(using = BlockCustomItemStore.Deserializer.class)
@Deprecated
public class BlockCustomItemStore {

    private static final org.bukkit.NamespacedKey ITEM_ID_KEY = new org.bukkit.NamespacedKey("wolfyutils", "item_id");

    private final NamespacedKey customItemKey;
    private UUID particleAnimationID;

    public BlockCustomItemStore(@NotNull CustomItem customItem, UUID particleAnimationID) {
        this.customItemKey = customItem.getNamespacedKey();
        this.particleAnimationID = particleAnimationID;
    }

    public BlockCustomItemStore(NamespacedKey customItemKey, UUID particleAnimationID) {
        this.customItemKey = customItemKey;
        this.particleAnimationID = particleAnimationID;
    }

    public NamespacedKey getCustomItemKey() {
        return customItemKey;
    }

    public CustomItem getCustomItem() {
        return WolfyUtilCore.getInstance().getRegistries().getCustomItems().get(customItemKey);
    }

    public UUID getParticleUUID() {
        return particleAnimationID;
    }

    public void setParticleUUID(@Nullable UUID particleUUID) {
        this.particleAnimationID = particleUUID;
    }

    static class Deserializer extends StdDeserializer<BlockCustomItemStore> {

        public Deserializer() {
            this(BlockCustomItemStore.class);
        }

        protected Deserializer(Class<BlockCustomItemStore> t) {
            super(t);
        }

        @Override
        public BlockCustomItemStore deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.readValueAsTree();
            if (node.has("key")) {
                var customItemKey = BukkitNamespacedKey.of(node.path("key").asText());
                if (customItemKey != null) {
                    return new BlockCustomItemStore(customItemKey, null);
                }
            }
            return null;
        }
    }
}
