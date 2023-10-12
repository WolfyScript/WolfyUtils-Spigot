package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.wolfyscript.utilities.Copyable;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public abstract class StackReference implements Copyable<StackReference> {

    private int customAmount = 1;
    private NamespacedKey parser;
    /**
     * Used to store the original stack
     */
    private ItemStack stack;
    /**
     * Used to store the previous parser result
     */
    private StackIdentifier identifier;
    private WolfyUtilCore core;

    protected StackReference(WolfyUtilCore core, NamespacedKey parser, ItemStack item) {
        this.core = core;
        this.parser = parser;
        this.stack = item;
        this.identifier = parseIdentifier();
    }

    private StackIdentifier parseIdentifier() {
        for (StackIdentifierParser<?> parser : core.getRegistries().getStackIdentifierParsers().sortedParsers()) {
            Optional<? extends StackIdentifier> identifierOptional = parser.from(stack);
            if (identifierOptional.isPresent()) return identifierOptional.get();
        }
        return new StackIdentifier() {
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
                return null;
            }

            @Override
            public NamespacedKey getNamespacedKey() {
                return new NamespacedKey("wolfyutils", "bukkit");
            }
        };
    }

    public StackIdentifier identifier() {
        return identifier;
    }

    public ItemStack stack() {
        return stack;
    }

}
