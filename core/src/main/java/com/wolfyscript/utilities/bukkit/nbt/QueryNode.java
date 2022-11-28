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

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTList;
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
import java.util.Optional;

@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@OptionalValueDeserializer(deserializer = QueryNode.OptionalValueDeserializer.class, delegateObjectDeserializer = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "type", defaultImpl = QueryNodeCompound.class)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = {"type"})
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

    /**
     * Reads the targeted value at the specified key of a parent NBTCompound.<br>
     *
     * @param path   The path of the <b>parent</b> NBTCompound.
     * @param key    The key of child to read.
     * @param parent The parent NBTCompound to read the child from.
     * @return Optional value that is read from the parent.
     */
    protected abstract Optional<VAL> readValue(String path, String key, NBTCompound parent);

    /**
     * Reads the targeted value at the specified index of a parent NBTList.<br>
     *
     * @param path   The path of the <b>parent</b> NBTList.
     * @param index  The index of child to read.
     * @param parent The parent NBTList to read the child from.
     * @return Optional value that is read from the parent.
     */
    protected Optional<VAL> readValue(String path, int index, NBTList<VAL> parent) {
        if (index < parent.size()) {
            return Optional.ofNullable(parent.get(index));
        }
        return Optional.empty();
    }

    public abstract boolean check(String key, NBTType nbtType, VAL value);

    /**
     * Applies the value to the specified key in the result NBTCompound.
     *
     * @param path            The path of the <b>parent</b> NBTCompound.
     * @param key             The key of child to apply the value for.
     * @param value           The available value read from the parent. (Read via {@link #readValue(String, String, NBTCompound)})
     * @param resultContainer The result NBTCompound to apply the value to. This compound is part of the container that will be returned once the query is completed.
     */
    protected abstract void applyValue(String path, String key, VAL value, NBTCompound resultContainer);

    /**
     * Applies the value to result NBTList.
     *
     * @param path       The path of the <b>parent</b> NBTList.
     * @param index      The index of child to apply the value for.
     * @param value      The available value read from the parent. (Read via {@link #readValue(String, int, NBTList)})
     * @param resultList The result NBTList to apply the value to. This list is part of the container that will be returned once the query is completed.
     */
    protected void applyValue(String path, int index, VAL value, NBTList<VAL> resultList) {
        resultList.add(value);
    }

    public final void visit(String path, String key, NBTCompound parent, NBTCompound resultContainer) {
        readValue(path, key, parent).filter(val -> check(key, parent.getType(key), val)).ifPresent(val -> applyValue(path, key, val, resultContainer));
    }

    public void visit(String path, int index, NBTList<VAL> parentList, NBTList<VAL> resultList) {
        readValue(path, index, parentList).filter(val -> check(key, parentList.getType(), val)).ifPresent(val -> applyValue(path, index, val, resultList));
    }

    @JsonGetter("type")
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

    public static Optional<QueryNode<?>> loadFrom(JsonNode node, String parentPath, String key) {
        var injectVars = new InjectableValues.Std();
        injectVars.addValue("key", key);
        injectVars.addValue("parent_path", parentPath);
        try {
            QueryNode<?> queryNode = WolfyCoreBukkit.getInstance().getWolfyUtils().getJacksonMapperUtil().getGlobalMapper().reader(injectVars).readValue(node, QueryNode.class);
            return Optional.ofNullable(queryNode);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public abstract QueryNode<VAL> copy();

    public static class OptionalValueDeserializer extends ValueDeserializer<QueryNode<?>> {

        public OptionalValueDeserializer() {
            super((Class<QueryNode<?>>) (Object) QueryNode.class);
        }

        @Override
        public QueryNode<?> deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
            if (jsonParser.isExpectedStartObjectToken()) {
                return null;
            }
            var token = jsonParser.currentToken();
            JsonNode node = null;
            NamespacedKey type = switch (token) {
                case VALUE_STRING -> {
                    node = jsonParser.readValueAsTree();
                    var text = node.asText();
                    yield switch (!text.isBlank() ? text.charAt(text.length() - 1) : 'S') {
                        case 'b', 'B' -> QueryNodeByte.TYPE;
                        case 's', 'S' -> QueryNodeShort.TYPE;
                        case 'i', 'I' -> QueryNodeInt.TYPE;
                        case 'l', 'L' -> QueryNodeLong.TYPE;
                        case 'f', 'F' -> QueryNodeFloat.TYPE;
                        case 'd', 'D' -> QueryNodeDouble.TYPE;
                        default -> QueryNodeString.TYPE;
                    };
                }
                case VALUE_NUMBER_INT -> QueryNodeInt.TYPE;
                case VALUE_NUMBER_FLOAT -> QueryNodeDouble.TYPE;
                case VALUE_FALSE, VALUE_TRUE -> QueryNodeBoolean.TYPE;
                default -> null;
            };
            if (type == null) return null;
            if (node == null) {
                node = jsonParser.readValueAsTree();
            }
            ObjectNode objNode = new ObjectNode(context.getNodeFactory());
            objNode.put("type", type.toString());
            objNode.set("value", node);
            return context.readTreeAsValue(objNode, QueryNode.class);
        }
    }

}
