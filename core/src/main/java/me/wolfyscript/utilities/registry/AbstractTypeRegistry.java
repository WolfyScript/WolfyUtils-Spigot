package me.wolfyscript.utilities.registry;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.KeyedStaticId;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Supplier;

public abstract class AbstractTypeRegistry<M extends Map<NamespacedKey, Class<? extends V>>, V extends Keyed> implements TypeRegistry<V> {

    protected final NamespacedKey key;
    protected final M map;

    public AbstractTypeRegistry(NamespacedKey key, M map, Registries registries) {
        this.key = key;
        this.map = map;
        registries.indexTypedRegistry(this);
    }

    public AbstractTypeRegistry(NamespacedKey key, Supplier<M> mapSupplier, Registries registries) {
        this(key, mapSupplier.get(), registries);
    }

    @Override
    public @Nullable Class<? extends V> get(@Nullable NamespacedKey key) {
        return map.get(key);
    }

    @Override
    public @Nullable V create(NamespacedKey key) {
        Class<? extends V> clazz = get(key);
        if (clazz != null) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void register(NamespacedKey key, V value) {
        register(key, (Class<V>) value.getClass());
    }

    @Override
    public void register(NamespacedKey key, Class<? extends V> value) {
        if (value != null) {
            Objects.requireNonNull(key, "Can't register value " + value.getName() + " because key is null!");
            Preconditions.checkState(!this.map.containsKey(key), "namespaced key '%s' already has an associated value!", key);
            map.put(key, value);
        }
    }

    @Override
    public void register(Class<? extends V> value) {
        KeyedStaticId staticIdAnnot = value.getAnnotation(KeyedStaticId.class);
        Preconditions.checkArgument(staticIdAnnot != null, "Value does not specify static id via the KeyedStaticId annotation!");
        NamespacedKey id = NamespacedKey.of(KeyedStaticId.KeyBuilder.createKeyString(value));
        register(id, value);
    }

    @Override
    public void register(V value) {
        register(value.getNamespacedKey(), (Class<? extends V>) value.getClass());
    }

    @NotNull
    @Override
    public Iterator<Class<? extends V>> iterator() {
        return map.values().iterator();
    }

    @Override
    public Set<NamespacedKey> keySet() {
        return Collections.unmodifiableSet(this.map.keySet());
    }

    @Override
    public Collection<Class<? extends V>> values() {
        return Collections.unmodifiableCollection(this.map.values());
    }

    @Override
    public Set<Map.Entry<NamespacedKey, Class<? extends V>>> entrySet() {
        return Collections.unmodifiableSet(this.map.entrySet());
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

}
