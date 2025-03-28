/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.registry;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.bukkit.items.CustomItemData;
import com.wolfyscript.utilities.bukkit.nbt.QueryNode;
import com.wolfyscript.utilities.bukkit.persistent.player.CustomPlayerData;
import com.wolfyscript.utilities.bukkit.persistent.world.CustomBlockData;
import com.wolfyscript.utilities.bukkit.registry.RegistryStackIdentifierParsers;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.Action;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.Event;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.Meta;
import me.wolfyscript.utilities.api.inventory.tags.Tags;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.eval.operators.Operator;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProvider;
import me.wolfyscript.utilities.util.particles.ParticleAnimation;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import me.wolfyscript.utilities.util.particles.animators.Animator;
import me.wolfyscript.utilities.util.particles.shapes.Shape;
import me.wolfyscript.utilities.util.particles.timer.Timer;

import java.util.HashMap;
import java.util.Map;

/**
 * Includes all the Registries inside WolfyUtilities.<br>
 * <br>
 * To use the registries you need to get an instance of this class.<br>
 * You should always try to not use the static method, as it can make the code less maintainable.<br>
 * If it is possible to access the instance of your WU API, then that should be used instead!<br>
 * <br>
 *
 * <strong>Get an instance:</strong>
 * <ul>
 *     <li>(<b>Recommended</b>) via your API instance {@link WolfyUtilities#getRegistries()}</li>
 *     <li>via static method {@link WolfyUtilCore#getInstance()} & {@link WolfyUtilCore#getRegistries()} (This should only be used in cases where you have no access to your API instance!)</li>
 * </ul>
 */
public class Registries {

    public static final NamespacedKey ITEM_ACTION_VALUES = NamespacedKey.wolfyutilties("custom_item/actions/values");
    public static final NamespacedKey ITEM_ACTION_TYPES = NamespacedKey.wolfyutilties("custom_item/actions/types");
    public static final NamespacedKey ITEM_EVENT_VALUES = NamespacedKey.wolfyutilties("custom_item/events/values");
    public static final NamespacedKey ITEM_EVENT_TYPES = NamespacedKey.wolfyutilties("custom_item/events/types");

    public static final NamespacedKey ITEM_CUSTOM_DATA = NamespacedKey.wolfyutilties("custom_item/data");
    public static final NamespacedKey ITEM_NBT_CHECKS = NamespacedKey.wolfyutilties("custom_item/nbt_checks");

    private final WolfyUtilCore core;

    private final Map<Class<? extends Keyed>, IRegistry<?>> REGISTRIES_BY_TYPE = new HashMap<>();
    private final Map<NamespacedKey, IRegistry<?>> REGISTRIES_BY_KEY = new HashMap<>();

    //Value registries
    private final RegistryCustomItem customItems;
    private final Registry<CustomData.Provider<?>> customItemData;
    private final RegistryParticleEffect particleEffects;
    private final RegistryParticleAnimation particleAnimations;
    private final Registry<Action<?>> customItemActionValues;
    private final Registry<Event<?>> customItemEventValues;
    private final RegistryStackIdentifierParsers stackIdentifierParsers;
    //Tags
    private final Tags<CustomItem> itemTags;
    //Class Registries
    private final TypeRegistry<Animator> particleAnimators;
    private final TypeRegistry<Shape> particleShapes;
    private final TypeRegistry<Timer> particleTimer;
    private final TypeRegistry<Meta> customItemNbtChecks;
    private final TypeRegistry<Action<?>> customItemActions;
    private final TypeRegistry<Event<?>> customItemEvents;

    private final TypeRegistry<CustomBlockData> customBlockData;
    private final TypeRegistry<CustomPlayerData> customPlayerData;
    private final TypeRegistry<CustomItemData> customItemDataTypeRegistry;
    private final TypeRegistry<StackIdentifier> stackIdentifierTypeRegistry;

    private final TypeRegistry<ValueProvider<?>> valueProviders;
    private final TypeRegistry<Operator> operators;

    private final TypeRegistry<QueryNode<?>> nbtQueryNodes;

    public Registries(WolfyUtilCore core) {
        this.core = core;

        customItems = new RegistryCustomItem(this);
        customItemData = new RegistrySimple<>(new NamespacedKey(core, "custom_item_data"), this);
        particleEffects = new RegistryParticleEffect(this);
        particleAnimations = new RegistryParticleAnimation(this);
        customItemActionValues = new RegistrySimple<>(ITEM_ACTION_VALUES, this, (Class<Action<?>>)(Object) Action.class);
        customItemEventValues = new RegistrySimple<>(ITEM_EVENT_VALUES, this, (Class<Event<?>>)(Object) Event.class);
        stackIdentifierParsers = new RegistryStackIdentifierParsers(this);

        itemTags = new Tags<>(this);

        particleAnimators = new UniqueTypeRegistrySimple<>(new NamespacedKey(core, "particles/animators"), this);
        particleShapes = new UniqueTypeRegistrySimple<>(new NamespacedKey(core, "particles/shapes"), this);
        particleTimer = new UniqueTypeRegistrySimple<>(new NamespacedKey(core, "particles/timers"), this);
        customItemNbtChecks = new UniqueTypeRegistrySimple<>(ITEM_NBT_CHECKS, this);
        customItemDataTypeRegistry = new UniqueTypeRegistrySimple<>(ITEM_CUSTOM_DATA, this);
        customItemActions = new UniqueTypeRegistrySimple<>(ITEM_ACTION_TYPES, this);
        customItemEvents = new UniqueTypeRegistrySimple<>(ITEM_EVENT_TYPES, this);

        stackIdentifierTypeRegistry = new UniqueTypeRegistrySimple<>(new NamespacedKey(core, "stack_identifier/types"), this);

        valueProviders = new UniqueTypeRegistrySimple<>(new NamespacedKey(core, "value_providers"), this);
        operators = new UniqueTypeRegistrySimple<>(new NamespacedKey(core, "operators"), this);

        customPlayerData = new UniqueTypeRegistrySimple<>(new NamespacedKey(core, "persistent/player"), this);
        customBlockData = new UniqueTypeRegistrySimple<>(new NamespacedKey(core, "persistent/block"), this);

        this.nbtQueryNodes = new UniqueTypeRegistrySimple<>(new NamespacedKey(core, "nbt/query/nodes"), this);
    }

    void indexTypedRegistry(IRegistry<?> registry) {
        Preconditions.checkArgument(!REGISTRIES_BY_KEY.containsKey(registry.getKey()), "A registry with the key \"" + registry.getKey() + "\" already exists!");
        REGISTRIES_BY_KEY.put(registry.getKey(), registry);

        //Index them by type if available
        if(registry instanceof RegistrySimple<?> simpleRegistry && simpleRegistry.getType() != null) {
            Preconditions.checkArgument(!REGISTRIES_BY_TYPE.containsKey(simpleRegistry.getType()), "A registry with that type already exists!");
            REGISTRIES_BY_TYPE.put(simpleRegistry.getType(), simpleRegistry);
        }
    }

    /**
     * Gets a Registry by the type it contains.
     * The Registry has to be created with the class of the type (See: {@link RegistrySimple#RegistrySimple(NamespacedKey, Registries, Class)}).
     *
     * @param type The class of the type the registry contains.
     * @param <V> The type the registry contains.
     * @return The registry of the specific type; or null if not available.
     */
    @SuppressWarnings("unchecked")
    public <V extends Keyed> Registry<V> getByType(Class<V> type) {
        return (Registry<V>) REGISTRIES_BY_TYPE.get(type);
    }

    public IRegistry<?> getByKey(NamespacedKey key) {
        return REGISTRIES_BY_KEY.get(key);
    }

    public <V extends IRegistry<?>> V getByKeyOfType(NamespacedKey key, Class<V> registryType) {
        var registry = getByKey(key);
        return registryType.cast(registry);
    }

    public WolfyUtilCore getCore() {
        return core;
    }

    /**
     * This Registry contains all the {@link CustomItem} instances.
     * If you install your own item make sure to use your plugins name as the namespace.
     */
    public RegistryCustomItem getCustomItems() {
        return customItems;
    }

    /**
     * Contains {@link CustomData.Provider} that can be used in any Custom Item from the point of registration.
     * <br>
     * You can register any CustomData you might want to add to your CustomItems and then save and load it from config too.
     * <br>
     * It allows you to save and load custom data into a CustomItem and makes things a lot easier if you have some items that perform specific actions with the data etc.
     * <br>
     * For example CustomCrafting registers its own CustomData, that isn't in this core API, for its Elite Workbenches that open up custom GUIs dependent on their CustomData.
     * And also the Recipe Book uses a CustomData object to store some data.
     * @return The registry of {@link CustomData.Provider}
     */
    public Registry<CustomData.Provider<?>> getCustomItemData() {
        return customItemData;
    }

    /**
     * Gets the registry containing all the available {@link ParticleAnimation}s.
     *
     * @return The type registry of {@link ParticleAnimation}s
     */
    public RegistryParticleAnimation getParticleAnimations() {
        return particleAnimations;
    }

    /**
     * Gets the registry containing all the available {@link ParticleEffect}s.
     *
     * @return The type registry of {@link ParticleEffect}s
     */
    public RegistryParticleEffect getParticleEffects() {
        return particleEffects;
    }

    public Tags<CustomItem> getItemTags() {
        return itemTags;
    }

    public Registry<Action<?>> getCustomItemActionValues() {
        return customItemActionValues;
    }

    public Registry<Event<?>> getCustomItemEventValues() {
        return customItemEventValues;
    }

    public TypeRegistry<Shape> getParticleShapes() {
        return particleShapes;
    }

    /**
     * Gets the registry containing all the available NBTChecks for CustomItems.
     *
     * @return The type registry of {@link Meta}
     */
    public TypeRegistry<Meta> getCustomItemNbtChecks() {
        return customItemNbtChecks;
    }

    public TypeRegistry<CustomItemData> getCustomItemDataTypeRegistry() {
        return customItemDataTypeRegistry;
    }

    /**
     * Gets the registry containing all the available Animators, that can be used in {@link ParticleAnimation}s.
     *
     * @return The type registry of {@link Animator}
     */
    public TypeRegistry<Animator> getParticleAnimators() {
        return particleAnimators;
    }

    /**
     * Gets the registry containing all the available Timers, that can be used in {@link ParticleAnimation}s.
     *
     * @return The type registry of {@link Timer}
     */
    public TypeRegistry<Timer> getParticleTimer() {
        return particleTimer;
    }

    public TypeRegistry<Action<?>> getCustomItemActions() {
        return customItemActions;
    }

    public TypeRegistry<Event<?>> getCustomItemEvents() {
        return customItemEvents;
    }

    public TypeRegistry<ValueProvider<?>> getValueProviders() {
        return valueProviders;
    }

    public TypeRegistry<Operator> getOperators() {
        return operators;
    }

    public TypeRegistry<CustomPlayerData> getCustomPlayerData() {
        return customPlayerData;
    }

    public TypeRegistry<CustomBlockData> getCustomBlockData() {
        return customBlockData;
    }

    public TypeRegistry<QueryNode<?>> getNbtQueryNodes() {
        return nbtQueryNodes;
    }

    public RegistryStackIdentifierParsers getStackIdentifierParsers() {
        return stackIdentifierParsers;
    }

    public TypeRegistry<StackIdentifier> getStackIdentifierTypeRegistry() {
        return stackIdentifierTypeRegistry;
    }
}
