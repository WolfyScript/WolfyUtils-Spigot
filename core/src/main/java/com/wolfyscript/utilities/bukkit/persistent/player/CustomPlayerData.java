package com.wolfyscript.utilities.bukkit.persistent.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import me.wolfyscript.utilities.registry.Registries;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

/**
 * Custom Data that can be applied to Players and persists across server restarts.<br>
 * <br>
 * How to create custom data:<br>
 * - Extend this class<br>
 * - Register class into Registry {@link Registries#getCustomPlayerData()}
 *
 */
@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "id")
@JsonPropertyOrder(value = {"id"})
public abstract class CustomPlayerData implements Keyed {

    @JsonProperty("id")
    private final NamespacedKey id;

    /**
     * The default constructor that must get the id of the custom data type.
     * @param id The id of the custom data type.
     */
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

    /**
     * Copies the custom data.
     * @return A deep-copy of this custom data.
     */
    public abstract CustomPlayerData copy();

    /**
     * Convenience method to get the player by uuid.
     *
     * @param uuid The uuid of the player.
     * @return The online player with that uuid; or empty optional if not online.
     */
    protected Optional<Player> getPlayer(UUID uuid) {
        return Optional.ofNullable(Bukkit.getPlayer(uuid));
    }

    @JsonIgnore
    @Override
    public NamespacedKey getNamespacedKey() {
        return id;
    }

}
