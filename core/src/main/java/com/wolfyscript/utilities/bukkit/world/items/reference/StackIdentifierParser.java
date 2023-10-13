package com.wolfyscript.utilities.bukkit.world.items.reference;

import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.util.Keyed;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface StackIdentifierParser<T extends StackIdentifier> extends Keyed, Comparable<StackIdentifierParser<T>> {

    int priority();

    Optional<T> from(ItemStack itemStack);

    @Override
    default int compareTo(@NotNull StackIdentifierParser<T> that) {
        return Integer.compare(this.priority(), that.priority());
    }

}
