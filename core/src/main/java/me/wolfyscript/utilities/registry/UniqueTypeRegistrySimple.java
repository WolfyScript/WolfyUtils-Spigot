package me.wolfyscript.utilities.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;

public class UniqueTypeRegistrySimple<V extends Keyed> extends AbstractTypeRegistry<BiMap<NamespacedKey, Class<? extends V>>, V> {

    public UniqueTypeRegistrySimple(NamespacedKey key, Registries registries) {
        super(key, HashBiMap.create(), registries);
    }

    public NamespacedKey getKey(Class<? extends V> value) {
        return map.inverse().get(value);
    }

}
