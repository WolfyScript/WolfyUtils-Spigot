package com.wolfyscript.utilities.bukkit.registry;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemReference;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemReferenceParserSettings;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.registry.Registries;
import com.wolfyscript.utilities.common.registry.UniqueTypeRegistrySimple;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;

public class RegistryItemReferences extends UniqueTypeRegistrySimple<ItemReference> {

    private final Map<NamespacedKey, ItemReferenceParserSettings.Creator.AbstractParser<?>> parserMap = new HashMap<>();
    private List<ItemReferenceParserSettings.Creator.AbstractParser<?>> priorityIndexedParsers;

    public RegistryItemReferences(Registries registries) {
        super(new BukkitNamespacedKey((WolfyCoreBukkit) registries.getCore(), "item_references"), registries);
    }

    @Override
    public void register(NamespacedKey key, Class<? extends ItemReference> value) {
        if (value != null) {
            Objects.requireNonNull(key, "Can't register value " + value.getName() + " because key is null!");
            Preconditions.checkState(!this.map.containsKey(key), "namespaced key '%s' already has an associated value!", key);
            map.put(key, value);
            ItemReferenceParserSettings.Creator.AbstractParser<?> parser = ItemReferenceParserSettings.Creator.constructParser(key, value);
            parserMap.put(key, parser);
            reIndexParsers();
        }
    }

    public ItemReference parse(ItemStack stack) {
        return parse(registries.getCore().getWolfyUtils(), stack);
    }

    public ItemReference parse(WolfyUtils wolfyUtils, ItemStack stack) {
        if (priorityIndexedParsers == null) {
            reIndexParsers();
        }
        Optional<? extends ItemReference> reference;
        for (ItemReferenceParserSettings.Creator.AbstractParser<?> parser : priorityIndexedParsers) {
            reference = parser.parseFromStack(wolfyUtils, stack);
            if (reference.isPresent()) {
                return reference.get();
            }
        }
        return null;
    }

    private void reIndexParsers() {
        priorityIndexedParsers = parserMap.values().stream().filter(Objects::nonNull).sorted().toList();
    }
}
