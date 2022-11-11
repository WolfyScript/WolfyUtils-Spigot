package com.wolfyscript.utilities.bukkit.persistent.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeResolver;

@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "id")
@JsonPropertyOrder(value = {"id"})
public abstract class CustomPlayerData implements Keyed {

    @JsonProperty("id")
    private final NamespacedKey id;

    protected CustomPlayerData(NamespacedKey id) {
        this.id = id;
    }

    /**
     * Called when the CustomPlayerData is initialising its data.
     * This happens right before it is added to the player, so the PlayerStorage does not yet contain it!
     */
    public abstract void onLoad();

    /**
     * Called when the CustomPlayerData is removed from the Player.
     * This happens right before it is removed from the player, so the PlayerStorage still contains it!
     */
    public abstract void onUnload();

    public abstract CustomPlayerData copy();

    @JsonIgnore
    @Override
    public NamespacedKey getNamespacedKey() {
        return id;
    }

}
