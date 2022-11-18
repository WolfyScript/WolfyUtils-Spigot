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
import me.wolfyscript.utilities.util.eval.value_providers.ValueProviderFloat;

public class QueryNodeFloat extends QueryNodePrimitive<Float> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("float");

    @JsonCreator
    public QueryNodeFloat(@JsonProperty("value") ValueProvider<Float> valueNode, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(TYPE, valueNode, key, parentPath);
        this.nbtType = NBTType.NBTTagFloat;
    }

    private QueryNodeFloat(QueryNodeFloat other) {
        super(other);
    }

    @Override
    protected Optional<Float> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getFloat(key));
    }

    @Override
    protected void applyValue(String path, String key, EvalContext context, Float value, NBTCompound resultContainer) {
        resultContainer.setFloat(key, value);
    }

    @Override
    public QueryNodeFloat copy() {
        return new QueryNodeFloat(this);
    }

}
