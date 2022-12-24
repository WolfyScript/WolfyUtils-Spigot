package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;

@ItemReferenceParserSettings(priority = Short.MIN_VALUE, parser = BukkitItemReference.Parser.class)
@KeyedStaticId(key = "bukkit")
public class BukkitItemReference extends ItemReference {

    private final ItemStack stack;

    @JsonCreator
    public BukkitItemReference(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("stack") ItemStack stack) {
        super(wolfyUtils);
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

    @Override
    public String toString() {
        return "BukkitItemReference{" +
                "stack=" + stack +
                "} " + super.toString();
    }

    public static class Parser implements ItemReference.Parser<BukkitItemReference> {

        @Override
        public Optional<BukkitItemReference> parseFromStack(WolfyUtils wolfyUtils, ItemStack stack) {
            return Optional.of(new BukkitItemReference(wolfyUtils, stack));
        }
    }


}
