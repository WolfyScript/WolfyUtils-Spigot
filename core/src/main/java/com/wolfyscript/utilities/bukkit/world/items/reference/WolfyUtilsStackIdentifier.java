package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.Optional;

@KeyedStaticId(key = "wolfyutils")
public class WolfyUtilsStackIdentifier implements StackIdentifier {

    private static final org.bukkit.NamespacedKey CUSTOM_ITEM_KEY = new org.bukkit.NamespacedKey(WolfyUtilities.getWUPlugin(), "custom_item");
    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("wolfyutils");

    private final NamespacedKey itemKey;

    @JsonCreator
    public WolfyUtilsStackIdentifier(@JsonProperty("item") NamespacedKey itemKey) {
        this.itemKey = itemKey;
    }

    @JsonGetter("item")
    public NamespacedKey itemKey() {
        return itemKey;
    }

    /**
     * Gets the stack this identifier references.
     * It uses the {@link CustomItem#create()} method to create the stack, or returns null if the referenced {@link CustomItem} is unavailable.
     *
     * @return The referenced ItemStack or null if referenced {@link CustomItem} is unavailable
     */
    @Override
    public ItemStack stack(ItemCreateContext context) {
        return customItem().map(customItem -> customItem.create(context.amount())).orElseGet(() -> {
            WolfyUtilities.getWUCore().getConsole().warn("Couldn't find CustomItem for " + itemKey.toString());
            return null;
        });
    }

    /**
     * Gets the {@link CustomItem} this identifier references.
     *
     * @return The referenced {@link CustomItem} of this identifier
     */
    public Optional<CustomItem> customItem() {
        return Optional.ofNullable(WolfyUtilCore.getInstance().getRegistries().getCustomItems().get(itemKey));
    }

    @Override
    public boolean matchesIgnoreCount(ItemStack other, boolean exact) {
        if (other == null) return false;
        var itemMeta = other.getItemMeta();
        if (itemMeta == null) return false;
        var container = itemMeta.getPersistentDataContainer();
        if (container.has(CUSTOM_ITEM_KEY, PersistentDataType.STRING)) {
            return Objects.equals(this.itemKey, NamespacedKey.of(container.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING)));
        }
        return false;
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

        @Override
        public DisplayConfiguration displayConfig() {
            return new DisplayConfiguration.SimpleDisplayConfig(
                    Component.text("WolfyUtils").color(NamedTextColor.DARK_AQUA).decorate(TextDecoration.BOLD),
                    new DisplayConfiguration.MaterialIconSettings(Material.CRAFTING_TABLE)
            );
        }
    }


}
