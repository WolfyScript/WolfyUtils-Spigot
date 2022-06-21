package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Optional;

public class QueryNodeDouble extends QueryNodePrimitive<Double> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("double");

    @JsonCreator
    public QueryNodeDouble(@JsonProperty("value") JsonNode valueNode, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(TYPE, valueNode.isTextual() ? Double.parseDouble(valueNode.asText().replaceAll("[dD]", "")) : valueNode.numberValue().doubleValue(), key, parentPath);
        this.nbtType = NBTType.NBTTagDouble;
    }

    private QueryNodeDouble(QueryNodeDouble other) {
        super(other);
    }

    @Override
    protected Optional<Double> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getDouble(key));
    }

    @Override
    protected void applyValue(String path, String key, Double value, NBTCompound resultContainer) {
        resultContainer.setDouble(key, value);
    }

    @Override
    public QueryNodeDouble copy() {
        return new QueryNodeDouble(this);
    }

}
