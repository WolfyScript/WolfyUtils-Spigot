package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Optional;

public class QueryNodeIntArray extends QueryNodePrimitive<int[]> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("int_array");

    @JsonCreator
    public QueryNodeIntArray(@JsonProperty("value") int[] value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(TYPE, value, key, parentPath);
        this.nbtType = NBTType.NBTTagByteArray;
    }

    public QueryNodeIntArray(QueryNodeIntArray other) {
        super(TYPE, other.value.clone(), other.key, other.parentPath);
    }

    @Override
    protected Optional<int[]> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getIntArray(key));
    }

    @Override
    protected void applyValue(String path, String key, int[] value, NBTCompound resultContainer) {
        resultContainer.setIntArray(key, value);
    }

    @Override
    public QueryNodeIntArray copy() {
        return new QueryNodeIntArray(this);
    }

}
