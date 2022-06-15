/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
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

package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeResolver;
import me.wolfyscript.utilities.util.json.jackson.ValueDeserializer;
import me.wolfyscript.utilities.util.json.jackson.annotations.OptionalValueDeserializer;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@OptionalValueDeserializer(deserializer = QueryNode.OptionalValueDeserializer.class, delegateObjectDeserializer = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "id")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = {"id"})
public abstract class QueryNode<VAL> implements Keyed {

    private static final String ERROR_MISMATCH = "Mismatched NBT types! Requested type: %s but found type %s, at node %s.%s";

    protected final NamespacedKey type;
    @JsonIgnore
    protected final String parentPath;
    @JsonIgnore
    protected final String key;
    @JsonIgnore
    protected NBTType nbtType = NBTType.NBTTagEnd;

    protected QueryNode(NamespacedKey type, @JacksonInject("key") String key, @JacksonInject("path") String parentPath) {
        this.type = type;
        this.parentPath = parentPath;
        this.key = key;
    }

    public abstract boolean check(String key, NBTType type, NBTCompound parent);

    protected abstract Optional<VAL> readValue(String key, NBTCompound parent);

    protected abstract NBTCompound visit(String path, String key, VAL value);

    public NBTCompound visit(String path, String key, NBTCompound parent) {
        NBTType nbtType = parent.getType(key);
        if (Objects.equals(nbtType, getNbtType())) {
            Optional<VAL> value = readValue(key, parent);
            if (value.isPresent()) {
                return visit(path, key, value.get());
            }
        }
        throw new RuntimeException(String.format(ERROR_MISMATCH, getNbtType(), nbtType, path, key));
    }

    @JsonGetter("id")
    public NamespacedKey getType() {
        return type;
    }

    public NBTType getNbtType() {
        return nbtType;
    }

    @JsonIgnore
    @Override
    public NamespacedKey getNamespacedKey() {
        return type;
    }

    //TODO
    static class OptionalValueDeserializer extends ValueDeserializer<QueryNode> {

        protected OptionalValueDeserializer() {
            super(QueryNode.class);
        }

        @Override
        public QueryNode deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.readValueAsTree();
            //Primitive
            ObjectNode objNode = new ObjectNode(context.getNodeFactory());
            objNode.put("id", "primitive");
            objNode.set("value", node);
            return context.readTreeAsValue(objNode, QueryNodePrimitive.class);

        }
    }

}
