package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Optional;

public class QueryNodeString extends QueryNodePrimitive<String> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("string");

    @JsonCreator
    public QueryNodeString(@JsonProperty("value") JsonNode valueNode, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(TYPE, valueNode.asText(), key, parentPath);
        this.nbtType = NBTType.NBTTagString;
    }

    @Override
    protected Optional<String> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getString(key));
    }

    @Override
    protected void applyValue(String path, String key, String value, NBTCompound resultContainer) {
        resultContainer.setString(key, value);
    }

}
