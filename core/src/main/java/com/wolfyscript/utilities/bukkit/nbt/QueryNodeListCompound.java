package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.List;

public class QueryNodeListCompound extends QueryNodeList<NBTCompound> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("list/compound");

    public QueryNodeListCompound(@JsonProperty("elements") List<Element<NBTCompound>> elements, @JacksonInject("key") String key, @JacksonInject("parent_path") String path) {
        super(TYPE, elements, key, path, NBTType.NBTTagCompound, NBTCompound.class);
    }

}
