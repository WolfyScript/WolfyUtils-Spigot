package com.wolfyscript.utilities.bukkit.world.items.reference;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.Optional;

public class WolfyUtilsStackIdentifier implements StackIdentifier {

    private static final org.bukkit.NamespacedKey CUSTOM_ITEM_KEY = new org.bukkit.NamespacedKey(WolfyUtilities.getWUPlugin(), "custom_item");
    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("wolfyutils");

    private final NamespacedKey namespacedKey;

    public WolfyUtilsStackIdentifier(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    /**
     * Gets the stack this identifier references.
     * It uses the {@link CustomItem#create()} method to create the stack, or returns null if the referenced {@link CustomItem} is unavailable.
     *
     * @return The referenced ItemStack or null if referenced {@link CustomItem} is unavailable
     */
    @Override
    public ItemStack item() {
        return customItem().map(CustomItem::create).orElseGet(() -> {
            WolfyUtilities.getWUCore().getConsole().warn("Couldn't find CustomItem for " + namespacedKey.toString());
            return null;
        });
    }

    /**
     * Gets the {@link CustomItem} this identifier references.
     *
     * @return The referenced {@link CustomItem} of this identifier
     */
    public Optional<CustomItem> customItem() {
        return Optional.ofNullable(WolfyUtilCore.getInstance().getRegistries().getCustomItems().get(namespacedKey));
    }

    @Override
    public boolean matches(ItemStack other, int count, boolean exact, boolean ignoreAmount) {
        if (other == null) return false;
        var itemMeta = other.getItemMeta();
        if (itemMeta == null) return false;
        var container = itemMeta.getPersistentDataContainer();
        if (container.has(CUSTOM_ITEM_KEY, PersistentDataType.STRING)) {
            return Objects.equals(this.namespacedKey, NamespacedKey.of(container.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING)));
        }
        return false;
    }

    @Override
    public StackIdentifierParser<?> parser() {
        return null;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return ID;
    }

    public static class Parser implements StackIdentifierParser<WolfyUtilsStackIdentifier> {

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public Optional<WolfyUtilsStackIdentifier> from(ItemStack itemStack) {
            if (itemStack == null) return Optional.empty();
            var itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                var container = itemMeta.getPersistentDataContainer();
                if (container.has(CUSTOM_ITEM_KEY, PersistentDataType.STRING)) {
                    return Optional.of(new WolfyUtilsStackIdentifier(NamespacedKey.of(container.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING))));
                }
            }
            return Optional.empty();
        }

        @Override
        public NamespacedKey getNamespacedKey() {
            return ID;
        }
    }


}
