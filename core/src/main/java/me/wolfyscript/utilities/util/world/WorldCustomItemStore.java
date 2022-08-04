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
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;

@JsonSerialize(using = WorldCustomItemStore.Serializer.class)
@JsonDeserialize(using = WorldCustomItemStore.Deserializer.class)
public class WorldCustomItemStore {

    @Deprecated
    public WorldCustomItemStore() {
    }

    @Deprecated
    public void store(Location location, CustomItem customItem) {
        setStore(location, new BlockCustomItemStore(customItem, null));
    }

    /**
     * Removes the stored block at this location and stops every active particle effect.
     *
     * @param location The target location of the block
     */
    @Deprecated
    public void remove(Location location) {
        if (location == null || location.getWorld() == null) return;
        ((WolfyCoreBukkit) WolfyCoreBukkit.getInstance()).getPersistentStorage().getOrCreateWorldStorage(location.getWorld()).removeBlock(location);
    }

    @Deprecated
    public boolean isStored(Location location) {
        if (location == null || location.getWorld() == null) return false;
        return ((WolfyCoreBukkit) WolfyCoreBukkit.getInstance()).getPersistentStorage().getOrCreateWorldStorage(location.getWorld()).getBlock(location).isPresent();
    }

    @Deprecated
    @Nullable
    public BlockCustomItemStore get(Location location) {
        if (location == null || location.getWorld() == null) return null;
        //TODO: return ((WolfyCoreBukkit) WolfyCoreBukkit.getInstance()).getPersistentStorage().getOrCreateWorldStorage(location.getWorld()).getBlock(location).orElse(null);
        return null;
    }

    @Deprecated
    public CustomItem getCustomItem(Location location) {
        BlockCustomItemStore blockStore = get(location);
        return blockStore != null ? blockStore.getCustomItem() : null;
    }

    /**
     * The current active particle effect on this Location.
     *
     * @param location The location to be checked.
     * @return The uuid of the currently active particle effect.
     */
    @Nullable
    @Deprecated
    public UUID getStoredEffect(@Nullable Location location) {
        BlockCustomItemStore blockStore = get(location);
        return blockStore != null ? blockStore.getParticleUUID() : null;
    }

    @Deprecated
    public boolean hasStoredEffect(Location location) {
        return isStored(location) && getStoredEffect(location) != null;
    }

    @Deprecated
    void setStore(Location location, BlockCustomItemStore blockStore) {
        if (location == null || location.getWorld() == null) return;
        //TODO: ((WolfyCoreBukkit) WolfyCoreBukkit.getInstance()).getPersistentStorage().getOrCreateWorldStorage(location.getWorld()).storeBlock(location, n);
    }

    @Deprecated
    public void initiateMissingBlockEffects() {
        //This is not doing anything anymore!
    }

    static class Serializer extends StdSerializer<WorldCustomItemStore> {

        public Serializer() {
            this(WorldCustomItemStore.class);
        }

        protected Serializer(Class<WorldCustomItemStore> t) {
            super(t);
        }

        @Override
        public void serialize(WorldCustomItemStore customItem, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartArray();
            gen.writeEndArray();
        }

    }

    static class Deserializer extends StdDeserializer<WorldCustomItemStore> {

        public Deserializer() {
            this(WorldCustomItemStore.class);
        }

        protected Deserializer(Class<WorldCustomItemStore> t) {
            super(t);
        }

        @Override
        public WorldCustomItemStore deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            var worldStore = new WorldCustomItemStore();
            JsonNode node = p.readValueAsTree();
            var mapper = JacksonUtil.getObjectMapper();
            node.elements().forEachRemaining(jsonNode -> {
                var location = mapper.convertValue(jsonNode.path("loc"), Location.class);
                var blockCustomItemStore = mapper.convertValue(jsonNode.path("store"), BlockCustomItemStore.class);
                if (location != null && blockCustomItemStore != null) {
                    worldStore.setStore(location, blockCustomItemStore);
                }
            });
            return worldStore;
        }
    }
}
