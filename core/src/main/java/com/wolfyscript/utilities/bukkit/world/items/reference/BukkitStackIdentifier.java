package com.wolfyscript.utilities.bukkit.world.items.reference;

import me.wolfyscript.utilities.util.NamespacedKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class BukkitStackIdentifier implements StackIdentifier {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("bukkit");

    private final ItemStack stack;

    public BukkitStackIdentifier(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        ItemStack cloned = stack.clone();
        cloned.setAmount(context.amount());
        return cloned;
    }

    @Override
    public boolean matches(ItemStack other, int count, boolean exact, boolean ignoreAmount) {
        if (other.getType() != stack.getType()) return false;
        if (!ignoreAmount && other.getAmount() < stack.getAmount() * count) return false;
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
            return Optional.of(new BukkitStackIdentifier(itemStack.clone()));
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
