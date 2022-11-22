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

public class QueryNodeByte extends QueryNodePrimitive<Byte> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("byte");

    @JsonCreator
    public QueryNodeByte(@JsonProperty("value") ValueProvider<Byte> value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(TYPE, value, key, parentPath);
        this.nbtType = NBTType.NBTTagByte;
    }

    private QueryNodeByte(QueryNodeByte other) {
        super(other);
    }

    @Override
    protected Optional<Byte> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getByte(key));
    }

    @Override
    protected void applyValue(String path, String key, EvalContext context, Byte value, NBTCompound resultContainer) {
        resultContainer.setByte(key, value);
    }

    @Override
    public QueryNodeByte copy() {
        return new QueryNodeByte(this);
    }

}
