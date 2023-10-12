package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface StackIdentifier extends Keyed {

    ItemStack item();

    default ItemStack item(Player player, World world) {
        return item();
    }

    boolean matches(ItemStack other);

    StackIdentifierParser<?> parser();

    @Override
    NamespacedKey getNamespacedKey();
}
