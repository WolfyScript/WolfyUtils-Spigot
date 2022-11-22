package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import java.util.Arrays;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Optional;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProvider;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProviderInteger;

public class QueryNodeIntArray extends QueryNodePrimitive<int[]> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("int_array");

    @JsonCreator
    public QueryNodeIntArray(@JsonProperty("value") ValueProvider<int[]> value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(TYPE, value, key, parentPath);
        this.nbtType = NBTType.NBTTagByteArray;
    }

    public QueryNodeIntArray(QueryNodeIntArray other) {
        super(TYPE, other.value, other.key, other.parentPath);
    }

    @Override
    protected Optional<int[]> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getIntArray(key));
    }

    @Override
    protected void applyValue(String path, String key, EvalContext context, int[] value, NBTCompound resultContainer) {
        resultContainer.setIntArray(key, value);
    }

    @Override
    public QueryNodeIntArray copy() {
        return new QueryNodeIntArray(this);
    }

}
