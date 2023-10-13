package com.wolfyscript.utilities.bukkit.world.items.reference;

import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.custom_items.references.VanillaRef;
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

    /**
     * Used for backwards compatibility with {@link APIReference}s.
     * By default, this returns a {@link VanillaRef} build using the {@link #item()} method.
     * Other implementations may choose to return their old related APIReference implementation.
     * <p>
     * Note that the new Identifiers are one level lower than APIReferences, so they no longer share the weight, and amount properties.
     * Those properties are now part of the higher level {@link StackReference}.
     * </p>
     *
     * @param weight The weight of the StackReference
     * @param amount The amount of the StackReference
     * @return The old APIReference, either implementation specific, or {@link VanillaRef} by default
     */
    @Deprecated
    default APIReference convert(double weight, int amount) {
        var ref = new VanillaRef(item());
        ref.setWeight(weight);
        ref.setAmount(amount);
        return ref;
    }
}
