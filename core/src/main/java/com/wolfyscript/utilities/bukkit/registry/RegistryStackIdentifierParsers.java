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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class RegistryStackIdentifierParsers extends RegistrySimple<StackIdentifierParser<?>> {

    private List<StackIdentifierParser<?>> priorityIndexedParsers = List.of();
    private final Registries registries;

    public RegistryStackIdentifierParsers(Registries registries) {
        super(new NamespacedKey(registries.getCore(), "stack_identifier/parsers"), registries, (Class<StackIdentifierParser<?>>) (Object) StackIdentifierParser.class);
        this.registries = registries;
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

    public List<StackIdentifierParser<?>> matchingParsers(ItemStack stack) {
        return sortedParsers().stream().sorted().filter(stackIdentifierParser -> stackIdentifierParser.from(stack).isPresent()).toList();
    }

    /**
     * @param stack
     * @return
     */
    public Optional<StackReference> parseFrom(ItemStack stack) {
        if (stack == null) return Optional.empty();
        return Optional.of(new StackReference(registries.getCore(), stack.getAmount(), 1d, parseIdentifier(stack), stack));
    }

    private void reIndexParsers() {
        Map<NamespacedKey, Integer> customPriorities = registries.getCore().getConfig().getIdentifierParserPriorities();

        priorityIndexedParsers = map.values().stream().filter(Objects::nonNull).sorted((parser, otherParser) -> {
            int parserPriority = customPriorities.getOrDefault(parser.getNamespacedKey(), parser.priority());
            int otherPriority = customPriorities.getOrDefault(otherParser.getNamespacedKey(), otherParser.priority());
            return Integer.compare(otherPriority, parserPriority);
        }).toList();
    }
}
