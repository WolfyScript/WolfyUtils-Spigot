package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderDouble;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Optional;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProvider;

public class QueryNodeDouble extends QueryNodePrimitive<Double> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("double");

    @JsonCreator
    public QueryNodeDouble(@JsonProperty("value") ValueProvider<Double> value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(TYPE, value, key, parentPath);
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
    protected void applyValue(String path, String key, EvalContext context, Double value, NBTCompound resultContainer) {
        resultContainer.setDouble(key, value);
    }

    @Override
    public QueryNodeDouble copy() {
        return new QueryNodeDouble(this);
    }

}
