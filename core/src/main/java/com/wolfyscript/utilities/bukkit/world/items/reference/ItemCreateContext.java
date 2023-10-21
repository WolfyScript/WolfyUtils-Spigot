package com.wolfyscript.utilities.bukkit.world.items.reference;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Optional;

public interface ItemCreateContext {

    int amount();

    default Optional<StackReference> reference() {
        return Optional.empty();
    }

    default Optional<Player> player() {
        return Optional.empty();
    }

    default Optional<World> world() {
        return Optional.empty();
    }

    /**
     * An empty implementation only containing the required values.
     *
     * @return An empty context only containing the required values
     */
    static ItemCreateContext empty(int amount) {
        return () -> amount;
    }



}
