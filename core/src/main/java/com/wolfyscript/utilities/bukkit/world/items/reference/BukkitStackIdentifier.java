package com.wolfyscript.utilities.bukkit.world.items.reference;

import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class BukkitStackIdentifier implements StackIdentifier {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("bukkit");

    private final ItemStack stack;
    private final Parser parser;

    public BukkitStackIdentifier(Parser parser, ItemStack stack) {
        this.stack = stack;
        this.parser = parser;
    }

    @Override
    public ItemStack item() {
        return stack;
    }

    @Override
    public ItemStack item(Player player, World world) {
        return stack;
    }

    @Override
    public boolean matches(ItemStack other) {
        return stack.isSimilar(other);
    }

    @Override
    public StackIdentifierParser<?> parser() {
        return parser;
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
            return Optional.of(new BukkitStackIdentifier(this, itemStack.clone()));
        }

        @Override
        public NamespacedKey getNamespacedKey() {
            return ID;
        }
    }

}
