package me.wolfyscript.utilities.api.nms.inventory;

import org.bukkit.World;
import org.bukkit.inventory.Inventory;

import java.util.function.BiFunction;

public interface FunctionalCookingRecipe {

    BiFunction<Inventory, World, Boolean> getMatcher();

    default boolean matches(Inventory inventory, World world) {
        return getMatcher().apply(inventory, world);
    }

}
