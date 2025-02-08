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

package me.wolfyscript.utilities.util.json.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.wolfyscript.utilities.json.jackson.MissingImplementationException;
import me.wolfyscript.utilities.registry.IRegistry;
import me.wolfyscript.utilities.registry.Registry;
import me.wolfyscript.utilities.registry.TypeRegistry;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.annotations.KeyedBaseType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class KeyedTypeIdResolver extends TypeIdResolverBase {

    private static final Map<Class<?>, IRegistry<?>> TYPE_REGISTRIES = new HashMap<>();

    private JavaType superType;

    /**
     * Registers a registry to be used for Json serialization and deserialization. <br>
     * To use that the class of the specified type must be annotated with {@link me.wolfyscript.utilities.util.json.jackson.annotations.OptionalKeyReference}.
     *
     * @param type The type to register.
     * @param registry The registry of the specified type.
     * @param <T> The type of the object.
     */
    public static <T extends Keyed> void registerTypeRegistry(Class<T> type, Registry<T> registry) {
        TYPE_REGISTRIES.putIfAbsent(type, registry);
    }

    /**
     * Registers a registry to be used for Json serialization and deserialization. <br>
     * To use that the class of the specified type must be annotated with {@link me.wolfyscript.utilities.util.json.jackson.annotations.OptionalKeyReference}.
     *
     * @param type The type to register.
     * @param registry The registry of the specified type.
     * @param <T> The type of the object.
     */
    public static <T extends Keyed> void registerTypeRegistry(Class<T> type, TypeRegistry<T> registry) {
        TYPE_REGISTRIES.putIfAbsent(type, registry);
    }

    @Override
    public void init(JavaType baseType) {
        superType = baseType;
    }

    @Override
    public String idFromValue(Object value) {
        return getKey(value);
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> aClass) {
        return getKey(value);
    }

    private String getKey(Object value) {
        if (value instanceof Keyed keyed) {
            return keyed.getNamespacedKey().toString();
        }
        throw new IllegalArgumentException(String.format("Object %s is not of type Keyed!", value.getClass().getName()));
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        Class<?> clazz = getTypeClass(NamespacedKey.of(id));
        return clazz != null ? context.constructSpecializedType(superType, clazz) : TypeFactory.unknownType();
    }

    @Nullable
    protected Class<?> getTypeClass(NamespacedKey key) {
        if (key != null) {
            Class<?> rawBaseClass = superType.getRawClass();
            //If it is specified, use the custom base type instead.
            KeyedBaseType baseTypeAnnot = rawBaseClass.getDeclaredAnnotation(KeyedBaseType.class);
            if (baseTypeAnnot != null) {
                rawBaseClass = baseTypeAnnot.baseType();
            }
            //Get the registry of the required base type
            var registry = TYPE_REGISTRIES.get(rawBaseClass);
            if (registry != null) {
                var object = registry.get(key);
                final Class<?> implClazz;
                if (object instanceof Class<?> classObj) {
                    implClazz = classObj;
                } else if(object instanceof Keyed) {
                    implClazz = object.getClass();
                } else {
                    throw new MissingImplementationException("Could not find implementation for " + key + " of type " + rawBaseClass.getName());
                }
                return implClazz;
            }
        }
        return null;
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
