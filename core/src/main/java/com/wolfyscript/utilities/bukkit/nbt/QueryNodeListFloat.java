package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.tr7zw.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.List;

public class QueryNodeListFloat extends QueryNodeList<Float> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("list/float");

    public QueryNodeListFloat(@JsonProperty("elements") List<Element<Float>> elements, @JacksonInject("key") String key, @JacksonInject("parent_path") String path) {
        super(TYPE, elements, key, path, NBTType.NBTTagByte, Float.class);
    }

    public QueryNodeListFloat(QueryNodeList<Float> other) {
        super(other);
    }

    @Override
    public QueryNodeListFloat copy() {
        return new QueryNodeListFloat(this);
    }
}
