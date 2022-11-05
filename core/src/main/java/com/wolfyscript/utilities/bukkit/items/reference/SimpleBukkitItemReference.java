package com.wolfyscript.utilities.bukkit.items.reference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.bukkit.items.BukkitItemStackConfig;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;

// At this moment of time, this reference does not save the complete ItemStack, therefor don't use it (Set priority lower than BukkitItemReference)!
@ItemReferenceParserSettings(priority = Short.MIN_VALUE)
public class SimpleBukkitItemReference extends ItemReference {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("simple");

    @JsonIgnore
    private ItemStack cachedStack;
    private final BukkitItemStackConfig config;

    @JsonCreator
    public SimpleBukkitItemReference(@JsonProperty("config") BukkitItemStackConfig config) {
        super(ID);
        this.config = config;
    }

    public SimpleBukkitItemReference(ItemStack itemStack) {
        this(new BukkitItemStackConfig(itemStack));
    }

    protected SimpleBukkitItemReference(SimpleBukkitItemReference reference) {
        super(reference);
        this.cachedStack = null;
        this.config = reference.config;
    }

    @Override
    public SimpleBukkitItemReference copy() {
        return new SimpleBukkitItemReference(this);
    }

    @Override
    public ItemStack getItem() {
        if (cachedStack == null) {
            cachedStack = config.constructItemStack();
        }
        return cachedStack;
    }

    @Override
    public boolean isValidItem(ItemStack other) {
        if (cachedStack == null) {
            cachedStack = config.constructItemStack();
        }
        return cachedStack.isSimilar(other);
    }

    public static SimpleBukkitItemReference parseFromStack(ItemStack itemStack) {
        return new SimpleBukkitItemReference(itemStack);
    }

}
