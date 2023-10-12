package com.wolfyscript.utilities.bukkit.world.items.reference;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.Optional;

public class WolfyUtilsStackIdentifier implements StackIdentifier {

    private static final org.bukkit.NamespacedKey CUSTOM_ITEM_KEY = new org.bukkit.NamespacedKey(WolfyUtilities.getWUPlugin(), "custom_item");
    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("wolfyutils");

    private final NamespacedKey namespacedKey;
    private final Parser parser;

    public WolfyUtilsStackIdentifier(Parser parser, NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
        this.parser = parser;
    }

    @Override
    public ItemStack item() {
        var customItem = WolfyUtilCore.getInstance().getRegistries().getCustomItems().get(namespacedKey);
        if (customItem != null) {
            return customItem.create();
        }
        WolfyUtilities.getWUCore().getConsole().warn("Couldn't find CustomItem for " + namespacedKey.toString());
        return null;
    }

    @Override
    public boolean matches(ItemStack other) {
        if (other != null) {
            var itemMeta = other.getItemMeta();
            if (itemMeta != null) {
                var container = itemMeta.getPersistentDataContainer();
                if (container.has(CUSTOM_ITEM_KEY, PersistentDataType.STRING)) {
                    return Objects.equals(this.namespacedKey, NamespacedKey.of(container.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING)));
                }
            }
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
                    return Optional.of(new WolfyUtilsStackIdentifier(this, NamespacedKey.of(container.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING))));
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
