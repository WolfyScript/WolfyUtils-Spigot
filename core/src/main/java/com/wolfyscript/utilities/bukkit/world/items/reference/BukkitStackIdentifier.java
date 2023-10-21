package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreImpl;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class BukkitStackIdentifier implements StackIdentifier {

    public static final NamespacedKey ID = BukkitStackIdentifier.wolfyutilties("bukkit");

    private final ItemStack stack;

    public BukkitStackIdentifier(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ItemStack item(ItemCreateContext context) {
        return stack;
    }

    @Override
    public ItemStack item(Player player, World world) {
        return stack;
    }

    @Override
    public boolean matches(ItemStack other, int count, boolean exact, boolean ignoreAmount) {
        if (other.getType() != stack.getType()) return false;
        if (!ignoreAmount && other.getAmount() < stack.getAmount() * count) return false;
        if (!stack.hasItemMeta() && !exact) return false;
        return stack.isSimilar(other);
    }

    @Override
    public StackIdentifierParser<?> parser() {
        return WolfyCoreImpl.getInstance().getRegistries().getStackIdentifierParsers().get(ID);
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
