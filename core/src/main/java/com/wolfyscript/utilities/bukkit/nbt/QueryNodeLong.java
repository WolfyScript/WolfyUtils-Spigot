package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Optional;

public class QueryNodeLong extends QueryNodePrimitive<Long> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("long");

    @JsonCreator
    public QueryNodeLong(@JsonProperty("value") JsonNode valueNode, String key, String parentPath) {
        super(TYPE, valueNode.isTextual() ? Long.parseLong(valueNode.asText().replaceAll("[lL]", "")) : valueNode.numberValue().longValue(), key, parentPath);
    }

    @Override
    protected Optional<Long> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getLong(key));
    }

    @Override
    protected void applyValue(String path, String key, Long value, NBTCompound resultContainer) {
        resultContainer.setLong(key, value);
    }

}
