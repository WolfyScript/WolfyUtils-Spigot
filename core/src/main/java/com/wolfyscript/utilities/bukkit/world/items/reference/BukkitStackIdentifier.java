package com.wolfyscript.utilities.bukkit.world.items.reference;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class BukkitStackIdentifier implements StackIdentifier {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("bukkit");

    private final ItemStack stack;

    public BukkitStackIdentifier(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ItemStack item() {
        return stack;
    }

    @Override
    public boolean matches(ItemStack other) {
        return stack.isSimilar(other);
    }

    @Override
    public StackIdentifierParser<?> parser() {
        return WolfyUtilCore.getInstance().getRegistries().getStackIdentifierParsers().get(ID);
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
    }

}
