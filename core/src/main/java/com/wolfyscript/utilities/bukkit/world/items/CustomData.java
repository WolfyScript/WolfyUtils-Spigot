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

package com.wolfyscript.utilities.bukkit.world.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @deprecated Replaced by {@link CustomItemData}
 */
@Deprecated
public abstract class CustomData implements Keyed {

    @JsonProperty("key")
    private final NamespacedKey namespacedKey;

    /**
     * This is the main constructor of this class and must not be changed in it's parameters!
     * If you do change it you also need to override the {@link Provider#createData()} method as it uses the NamespacedKey constructor by default!
     *
     * @param namespacedKey The {@link BukkitNamespacedKey} of this CustomData. Parsed to this class from the {@link Provider}.
     */
    protected CustomData(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    protected CustomData(CustomData customData) {
        this.namespacedKey = customData.namespacedKey;
    }

    /**
     * Called whenever this data object is saved to the {@link CustomItem} config.
     *
     * @param customItem The {@link CustomItem} that this data is currently saved into.
     * @param gen        The {@link JsonGenerator} of the serialization.
     * @param provider   The {@link SerializerProvider} of the serialization.
     * @throws IOException If there occurs an error while writing it to the config.
     */
    public abstract void writeToJson(CustomItem customItem, JsonGenerator gen, SerializerProvider provider) throws IOException;

    /**
     * Called whenever this data is loaded from the config of the {@link CustomItem}.
     *
     * @param customItem The {@link CustomItem} that this data is currently loaded for and will be added to.
     * @param node       The {@link JsonNode} that is loaded from the config.
     * @param context    The {@link DeserializationContext} from the deserialization.
     * @throws IOException If there occurs an error while loading.
     */
    protected abstract void readFromJson(CustomItem customItem, JsonNode node, DeserializationContext context) throws IOException;

    /**
     * @return The namespacedKey, that is passed to this CustomData by it's {@link Provider}
     */
    @Override
    public final NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomData that = (CustomData) o;
        return Objects.equals(namespacedKey, that.namespacedKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespacedKey);
    }

    /**
     * Your CustomData should implement this method to be cloned correctly using a copy constructor.<br>
     * <strong>If you don't override this method it will just return the original instance! Future update (1.6.4.0) will require this method to be implemented!</strong>
     *
     * @return A cloned instance if overridden else just returns this instance.
     */
    @Override
    public CustomData clone() {
        return this;
    }

    /**
     * The Provider's goal is to create a new instance of the CustomData and load it from config.
     *
     * @param <T> The CustomData type that this Provider is used for.
     */
    public static class Provider<T extends CustomData> implements Keyed {

        private final NamespacedKey namespacedKey;
        private final Class<T> customDataClass;

        /**
         * Creates a new provider for the {@link CustomData} of the specified class.
         * This Provider requires a unique namespace and key, so it doesn't interfere with other data.
         *
         * @param namespacedKey   Unique {@link BukkitNamespacedKey} to identify this provider and it's data!
         * @param customDataClass Class of the overridden {@link CustomData} class.
         */
        public Provider(NamespacedKey namespacedKey, Class<T> customDataClass) {
            this.namespacedKey = namespacedKey;
            this.customDataClass = customDataClass;
        }

        /**
         * @return The {@link BukkitNamespacedKey} of this Provider. This method and the {@link CustomData#getNamespacedKey()} should always return the same value!
         */
        public NamespacedKey getNamespacedKey() {
            return namespacedKey;
        }

        /**
         * Adds a new instance of the {@link CustomData}, that is loaded from the config, to the {@link CustomItem}.
         *
         * @param customItem The {@link CustomItem} to add the data to.
         * @param node       The {@link JsonNode} loaded from the config.
         * @param context    The {@link DeserializationContext} from the deserialization.
         */
        public void addData(CustomItem customItem, JsonNode node, DeserializationContext context) {
            try {
                T instance = createData();
                instance.readFromJson(customItem, node, context);
                customItem.addCustomData(namespacedKey, instance);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Creates a new default instance of the {@link CustomData} object.
         *
         * @return The new default instance of the {@link CustomData} object.
         */
        public T createData() {
            try {
                Constructor<T> constructor = customDataClass.getDeclaredConstructor(NamespacedKey.class);
                constructor.setAccessible(true);
                return constructor.newInstance(namespacedKey);
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException |
                     IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    @Deprecated
    public static class DeprecatedCustomDataWrapper extends HashMap<NamespacedKey, CustomData> {

        protected CustomItem owner;

        public DeprecatedCustomDataWrapper(CustomItem owner) {
            this.owner = owner;
        }

    }

    public static class Serializer extends StdSerializer<DeprecatedCustomDataWrapper> {

        public Serializer() {
            super(DeprecatedCustomDataWrapper.class);
        }

        @Override
        public void serialize(DeprecatedCustomDataWrapper dataMap, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            for (Map.Entry<NamespacedKey, CustomData> value : dataMap.entrySet()) {
                gen.writeObjectFieldStart(value.getKey().toString());
                value.getValue().writeToJson(dataMap.owner, gen, provider);
                gen.writeEndObject();
            }
            gen.writeEndObject();
        }
    }
}

