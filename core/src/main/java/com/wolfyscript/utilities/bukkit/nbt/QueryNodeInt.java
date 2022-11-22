package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Optional;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProvider;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProviderInteger;

public class QueryNodeInt extends QueryNodePrimitive<Integer> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("int");

    @JsonCreator
    public QueryNodeInt(@JsonProperty("value") ValueProvider<Integer> value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(TYPE, value, key, parentPath);
        this.nbtType = NBTType.NBTTagInt;
    }

    public QueryNodeInt(QueryNodePrimitive<Integer> other) {
        super(other);
    }

    @Override
    protected Optional<Integer> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getInteger(key));
    }

    @Override
    protected void applyValue(String path, String key, EvalContext context, Integer value, NBTCompound resultContainer) {
        resultContainer.setInteger(key, value);
    }

    @Override
    public QueryNode<Integer> copy() {
        return new QueryNodeInt(this);
    }

}
