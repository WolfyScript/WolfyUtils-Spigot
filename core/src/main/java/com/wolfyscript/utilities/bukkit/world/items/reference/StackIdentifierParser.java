package com.wolfyscript.utilities.bukkit.world.items.reference;

import me.wolfyscript.utilities.util.Keyed;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public interface StackIdentifierParser<T extends StackIdentifier> extends Keyed {

    int priority();

    Optional<T> from(ItemStack itemStack);

}
