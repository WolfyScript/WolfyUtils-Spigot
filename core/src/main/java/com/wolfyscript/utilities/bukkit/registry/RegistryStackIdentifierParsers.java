package com.wolfyscript.utilities.bukkit.registry;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.bukkit.world.items.reference.BukkitStackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackReference;
import me.wolfyscript.utilities.registry.Registries;
import me.wolfyscript.utilities.registry.RegistrySimple;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RegistryStackIdentifierParsers extends RegistrySimple<StackIdentifierParser<?>> {

    private List<StackIdentifierParser<?>> priorityIndexedParsers = List.of();

    public RegistryStackIdentifierParsers(Registries registries) {
        super(new NamespacedKey(registries.getCore(), "stack_identifier/parsers"), registries, (Class<StackIdentifierParser<?>>)(Object) StackIdentifierParser.class);
    }

    @Override
    public void register(NamespacedKey namespacedKey, StackIdentifierParser<?> value) {
        if (value != null) {
            Preconditions.checkState(!this.map.containsKey(namespacedKey), "namespaced key '%s' already has an associated value!", namespacedKey);
            map.put(namespacedKey, value);
            reIndexParsers();
        }
    }

    public List<StackIdentifierParser<?>> sortedParsers() {
        return priorityIndexedParsers;
    }

    public StackIdentifier parseIdentifier(ItemStack stack) {
        for (StackIdentifierParser<?> parser : sortedParsers()) {
            Optional<? extends StackIdentifier> identifierOptional = parser.from(stack);
            if (identifierOptional.isPresent()) return identifierOptional.get();
        }
        return new BukkitStackIdentifier(stack);
    }

    private void reIndexParsers() {
        priorityIndexedParsers = map.values().stream().filter(Objects::nonNull).sorted().toList();
    }
}