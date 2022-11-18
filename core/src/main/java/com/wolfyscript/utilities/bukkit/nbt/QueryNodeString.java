package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Optional;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProvider;

public class QueryNodeString extends QueryNodePrimitive<String> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("string");

    @JsonCreator
    public QueryNodeString(@JsonProperty("value") ValueProvider<String> value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(TYPE, value, key, parentPath);
        this.nbtType = NBTType.NBTTagString;
    }

    public QueryNodeString(QueryNodePrimitive<String> other) {
        super(other);
    }

    @Override
    protected Optional<String> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getString(key));
    }

    @Override
    protected void applyValue(String path, String key, EvalContext context, String value, NBTCompound resultContainer) {
        resultContainer.setString(key, value);
    }

    @Override
    public QueryNodeString copy() {
        return new QueryNodeString(this);
    }

}
