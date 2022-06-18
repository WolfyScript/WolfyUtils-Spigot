package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.tr7zw.changeme.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.List;

public class QueryNodeListLong extends QueryNodeList<Long> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("list/long");

    public QueryNodeListLong(@JsonProperty("elements") List<Element<Long>> elements, @JacksonInject("key") String key, @JacksonInject("parent_path") String path) {
        super(TYPE, elements, key, path, NBTType.NBTTagByte, Long.class);
    }

}
