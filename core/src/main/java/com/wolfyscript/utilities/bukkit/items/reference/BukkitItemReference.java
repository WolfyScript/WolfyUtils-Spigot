package com.wolfyscript.utilities.bukkit.items.reference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;

@ItemReferenceParserSettings(priority = -1000)
public class BukkitItemReference extends ItemReference {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("bukkit");

    private final ItemStack stack;

    @JsonCreator
    public BukkitItemReference(@JsonProperty("stack") ItemStack stack) {
        super(ID);
        this.stack = stack;
    }

    protected BukkitItemReference(BukkitItemReference reference) {
        super(reference);
        this.stack = reference.stack;
    }

    @Override
    public BukkitItemReference copy() {
        return new BukkitItemReference(this);
    }

    @Override
    public ItemStack getItem() {
        return stack;
    }

    @Override
    public boolean isValidItem(ItemStack other) {
        return stack.isSimilar(other);
    }

    public static BukkitItemReference parseFromStack(ItemStack itemStack) {
        return new BukkitItemReference(itemStack);
    }

}
