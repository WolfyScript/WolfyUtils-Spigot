package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.tr7zw.nbtapi.NBTList;
import de.tr7zw.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.List;

public class QueryNodeListDouble extends QueryNodeList<Double> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("list/double");

    public QueryNodeListDouble(@JsonProperty("elements") List<Element<Double>> elements, @JacksonInject("key") String key, @JacksonInject("parent_path") String path) {
        super(TYPE, elements, key, path, NBTType.NBTTagByte, Double.class);
    }

    public QueryNodeListDouble(QueryNodeList<Double> other) {
        super(other);
    }

    @Override
    public QueryNodeListDouble copy() {
        return new QueryNodeListDouble(this);
    }
}
