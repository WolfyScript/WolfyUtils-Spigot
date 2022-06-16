package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Optional;

public class QueryNodeByte extends QueryNodePrimitive<Byte> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("byte");

    @JsonCreator
    public QueryNodeByte(@JsonProperty("value") JsonNode valueNode, String key, String parentPath) {
        super(TYPE, valueNode.isTextual() ? Byte.parseByte(valueNode.asText().replaceAll("[bB]", "")) : valueNode.numberValue().byteValue(), key, parentPath);
    }

    @Override
    protected Optional<Byte> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getByte(key));
    }

    @Override
    protected void applyValue(String path, String key, Byte value, NBTCompound resultContainer) {
        resultContainer.setByte(key, value);
    }

}
