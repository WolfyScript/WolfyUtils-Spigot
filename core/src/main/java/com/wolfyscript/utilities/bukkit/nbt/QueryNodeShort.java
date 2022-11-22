package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderShort;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Optional;
import me.wolfyscript.utilities.util.eval.context.EvalContext;

public class QueryNodeShort extends QueryNodePrimitive<Short> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("short");

    @JsonCreator
    public QueryNodeShort(@JsonProperty("value") ValueProviderShort value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(TYPE, value, key, parentPath);
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
    protected void applyValue(String path, String key, EvalContext context, Short value, NBTCompound resultContainer) {
        resultContainer.setShort(key, value);
    }

    @Override
    public QueryNodeShort copy() {
        return new QueryNodeShort(this);
    }

}
