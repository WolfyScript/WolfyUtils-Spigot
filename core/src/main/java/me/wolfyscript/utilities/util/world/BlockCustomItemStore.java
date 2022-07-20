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

package me.wolfyscript.utilities.util.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonSerialize(using = BlockCustomItemStore.Serializer.class)
@JsonDeserialize(using = BlockCustomItemStore.Deserializer.class)
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

    public static class PersistentType implements PersistentDataType<PersistentDataContainer, BlockCustomItemStore> {

        @NotNull
        @Override
        public Class<PersistentDataContainer> getPrimitiveType() {
            return PersistentDataContainer.class;
        }

        @NotNull
        @Override
        public Class<BlockCustomItemStore> getComplexType() {
            return BlockCustomItemStore.class;
        }

        @NotNull
        @Override
        public PersistentDataContainer toPrimitive(@NotNull BlockCustomItemStore complex, @NotNull PersistentDataAdapterContext context) {
            PersistentDataContainer data = context.newPersistentDataContainer();
            data.set(ITEM_ID_KEY, PersistentDataType.STRING, complex.customItemKey != null ? complex.customItemKey.toString() : "");
            return data;
        }

        @NotNull
        @Override
        public BlockCustomItemStore fromPrimitive(@NotNull PersistentDataContainer data, @NotNull PersistentDataAdapterContext context) {
            String customItem = data.getOrDefault(ITEM_ID_KEY, PersistentDataType.STRING, "");
            if (customItem.isBlank()) return new BlockCustomItemStore((NamespacedKey) null, null);
            var customItemKey = NamespacedKey.of(customItem);
            if (customItemKey != null) {
                return new BlockCustomItemStore(customItemKey, null);
            }
            return new BlockCustomItemStore((NamespacedKey) null, null);
        }
    }


    static class Serializer extends StdSerializer<BlockCustomItemStore> {

        public Serializer() {
            this(BlockCustomItemStore.class);
        }

        protected Serializer(Class<BlockCustomItemStore> t) {
            super(t);
        }

        @Override
        public void serialize(BlockCustomItemStore blockStore, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("key", blockStore.getCustomItemKey().toString());
            gen.writeEndObject();
        }

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
                var customItemKey = NamespacedKey.of(node.path("key").asText());
                if (customItemKey != null) {
                    return new BlockCustomItemStore(customItemKey, null);
                }
            }
            return null;
        }
    }
}
