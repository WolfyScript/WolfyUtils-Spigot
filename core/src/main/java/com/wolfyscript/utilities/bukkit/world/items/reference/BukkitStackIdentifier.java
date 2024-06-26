package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import me.wolfyscript.utilities.util.NamespacedKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

@KeyedStaticId(key = "bukkit")
public class BukkitStackIdentifier implements StackIdentifier {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("bukkit");

    private final ItemStack stack;

    @JsonCreator
    public BukkitStackIdentifier(@JsonProperty("stack") ItemStack stack) {
        this.stack = stack;
    }

    @JsonGetter("stack")
    public ItemStack stack() {
        return stack;
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        ItemStack cloned = stack.clone();
        cloned.setAmount(context.amount());
        return cloned;
    }

    @Override
    public boolean matchesIgnoreCount(ItemStack other, boolean exact) {
        if (other == null) return false;
        if (!Objects.equals(stack.getType(), other.getType())) return false;
        if (stack.hasItemMeta() || exact) return stack.isSimilar(other);
        return true;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return ID;
    }

    public static class Parser implements StackIdentifierParser<BukkitStackIdentifier> {

        @Override
        public int priority() {
            return -2048;
        }

        @Override
        public Optional<BukkitStackIdentifier> from(ItemStack itemStack) {
            if (itemStack == null) return Optional.of(new BukkitStackIdentifier(new ItemStack(Material.AIR)));
            ItemStack copy = itemStack.clone();
            copy.setAmount(1); // The identifiers should only have a stack of 1, the amount is handled by the StackReference
            return Optional.of(new BukkitStackIdentifier(copy));
        }

        @Override
        public NamespacedKey getNamespacedKey() {
            return ID;
        }

        @Override
        public DisplayConfiguration displayConfig() {
            return new DisplayConfiguration.SimpleDisplayConfig(
                    Component.text("Bukkit").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD),
                    new DisplayConfiguration.MaterialIconSettings(Material.LAVA_BUCKET)
            );
        }
    }

}
