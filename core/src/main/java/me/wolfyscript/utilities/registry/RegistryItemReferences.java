package me.wolfyscript.utilities.registry;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.bukkit.items.reference.ItemReference;
import com.wolfyscript.utilities.bukkit.items.reference.ItemReferenceParserSettings;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class RegistryItemReferences extends UniqueTypeRegistrySimple<ItemReference> {

    private final Map<NamespacedKey, ItemReference.Parser<?>> parserMap = new HashMap<>();
    private List<ItemReference.Parser<?>> priorityIndexedParsers = new ArrayList<>();

    public RegistryItemReferences(Registries registries) {
        super(new NamespacedKey(registries.getCore(), "item_references"), registries);
    }

    @Override
    public void register(NamespacedKey key, Class<? extends ItemReference> value) {
        if (value != null) {
            Objects.requireNonNull(key, "Can't register value " + value.getName() + " because key is null!");
            Preconditions.checkState(!this.map.containsKey(key), "namespaced key '%s' already has an associated value!", key);
            map.put(key, value);
            ItemReference.Parser<?> parser = ItemReferenceParserSettings.Creator.constructParser(key, value);
            parserMap.put(key, parser);
            reIndexParsers();
        }
    }

    public ItemReference parse(ItemStack stack) {
        if (priorityIndexedParsers == null) {
            reIndexParsers();
        }
        ItemReference reference;
        for (ItemReference.Parser<?> parser : priorityIndexedParsers) {
            reference = parser.parseFromStack(stack);
            if (reference != null) {
                return reference;
            }
        }
        return null;
    }

    private void reIndexParsers() {
        priorityIndexedParsers = parserMap.values().stream().filter(Objects::nonNull).sorted(Comparator.comparingInt(ItemReference.Parser::getPriority)).toList();
    }
}
