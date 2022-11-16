package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Optional;

public class QueryNodeShort extends QueryNodePrimitive<Short> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("short");

    @JsonCreator
    public QueryNodeShort(@JsonProperty("value") JsonNode valueNode, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(TYPE, valueNode.isTextual() ? Short.parseShort(valueNode.asText().replaceAll("[sS]", "")) : valueNode.numberValue().shortValue(), key, parentPath);
        this.nbtType = NBTType.NBTTagShort;
    }

    public QueryNodeShort(QueryNodePrimitive<Short> other) {
        super(other);
    }

    @Override
    protected Optional<Short> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getShort(key));
    }

    @Override
    protected void applyValue(String path, String key, Short value, NBTCompound resultContainer) {
        resultContainer.setShort(key, value);
    }

    @Override
    public QueryNodeShort copy() {
        return new QueryNodeShort(this);
    }

}
