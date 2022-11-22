package com.wolfyscript.utilities.bukkit.items;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.wolfyscript.utilities.Copyable;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.json.KeyedTypeIdResolver;
import com.wolfyscript.utilities.json.KeyedTypeResolver;

/**
 * This is the direct replacement for the old {@link me.wolfyscript.utilities.api.inventory.custom_items.CustomData}
 *
 */
@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "id")
@JsonPropertyOrder(value = {"id"})
public abstract class CustomItemData implements Keyed, Copyable<CustomItemData> {

    @JsonProperty("id")
    private final BukkitNamespacedKey id;

    protected CustomItemData(BukkitNamespacedKey id) {
        this.id = id;
    }

    @JsonIgnore
    @Override
    public BukkitNamespacedKey getNamespacedKey() {
        return id;
    }

}
