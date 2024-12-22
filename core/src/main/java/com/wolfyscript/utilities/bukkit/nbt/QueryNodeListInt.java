package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.tr7zw.nbtapi.NBTList;
import de.tr7zw.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.List;

public class QueryNodeListInt extends QueryNodeList<Integer> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("list/int");

    public QueryNodeListInt(@JsonProperty("elements") List<Element<Integer>> elements, @JacksonInject("key") String key, @JacksonInject("parent_path") String path) {
        super(TYPE, elements, key, path, NBTType.NBTTagByte, Integer.class);
    }

    public QueryNodeListInt(QueryNodeList<Integer> other) {
        super(other);
    }

    @Override
    public QueryNodeListInt copy() {
        return new QueryNodeListInt(this);
    }
}
