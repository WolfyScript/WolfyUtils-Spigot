package com.wolfyscript.utilities.bukkit.persistent.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Optional;
import me.wolfyscript.utilities.registry.Registries;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

/**
 * This data is used to store persistent data on Blocks.<br>
 * The data is saved directly inside the Chunks' {@link org.bukkit.persistence.PersistentDataContainer}, so it persists across server restarts.<br>
 * In order to save it the {@link CustomBlockData} is serialized into a JsonString using Jackson.<br>
 * The String is then saved into the {@link org.bukkit.persistence.PersistentDataContainer} with the id ({@link org.bukkit.NamespacedKey}) as the key.<br>
 * <br>
 * On Deserialization the key is used to find the registered data type (See {@link Registries#getCustomBlockData()})<br>
 * The String content is then deserialized to that type using Jackson.<br>
 * There are injectable values that can be used in the constructor to get access to the Core, ChunkStorage, Position, etc.<br>
 * <ul>
 *     <li>{@link me.wolfyscript.utilities.api.WolfyUtilCore}</li>
 *     <li>{@link ChunkStorage}</li>
 *     <li>{@link org.bukkit.util.Vector}</li>
 * </ul>
 * That can be injected using the {@link com.fasterxml.jackson.annotation.JacksonInject} annotation.<br>
 * <br>
 * One of the default data, that stores the CustomItems on blocks is {@link com.wolfyscript.utilities.bukkit.items.CustomItemBlockData}
 */
public abstract class CustomBlockData implements Keyed {

    private final NamespacedKey id;

    protected CustomBlockData(NamespacedKey id) {
        this.id = id;
    }

    /**
     * Called when the BlockStorage is initialising its data.
     * Usually right after the data was constructed.
     */
    public abstract void onLoad();

    /**
     * Called when the BlockStorage is removed from the ChunkStorage or the Chunk is unloaded.
     */
    public abstract void onUnload();

    public abstract CustomBlockData copy();

    /**
     * Copies this data to the other BlockStorage.
     *
     * @param storage The other BlockStorage to copy the data to.
     * @return The data that was copied to the other BlockStorage.
     */
    public abstract CustomBlockData copyTo(BlockStorage storage);

    @JsonIgnore
    @Override
    public NamespacedKey getNamespacedKey() {
        return id;
    }
}
