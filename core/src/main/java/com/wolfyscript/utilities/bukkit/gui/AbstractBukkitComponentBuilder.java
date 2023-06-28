package com.wolfyscript.utilities.bukkit.gui;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentBuilder;
import com.wolfyscript.utilities.json.annotations.KeyedBaseType;
import java.util.List;

@KeyedBaseType(baseType = ComponentBuilder.class)
public abstract class AbstractBukkitComponentBuilder<OWNER extends Component, PARENT> implements ComponentBuilder<OWNER, PARENT> {

    @JsonProperty("type")
    private final NamespacedKey type;
    private final String id;
    @JsonProperty("slots")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private final List<Integer> slots;
    private final WolfyUtils wolfyUtils;

    protected AbstractBukkitComponentBuilder(String id, WolfyUtils wolfyUtils, List<Integer> slots) {
        this.type = wolfyUtils.getIdentifiers().getNamespaced(getClass());
        this.id = id;
        this.wolfyUtils = wolfyUtils;
        this.slots = slots;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public List<Integer> getSlots() {
        return slots;
    }

    protected WolfyUtils getWolfyUtils() {
        return wolfyUtils;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return type;
    }

}
