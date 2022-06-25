package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Optional;

public class QueryNodeByteArray extends QueryNodePrimitive<byte[]> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("byte_array");

    @JsonCreator
    public QueryNodeByteArray(@JsonProperty("value") byte[] value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(TYPE, value, key, parentPath);
        this.nbtType = NBTType.NBTTagByteArray;
    }

    private QueryNodeByteArray(QueryNodeByteArray other) {
        super(TYPE, other.value.clone(), other.key, other.parentPath);
    }

    @Override
    protected Optional<byte[]> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getByteArray(key));
    }

    @Override
    protected void applyValue(String path, String key, byte[] value, NBTCompound resultContainer) {
        resultContainer.setByteArray(key, value);
    }

    @Override
    public QueryNodeByteArray copy() {
        return new QueryNodeByteArray(this);
    }

}
