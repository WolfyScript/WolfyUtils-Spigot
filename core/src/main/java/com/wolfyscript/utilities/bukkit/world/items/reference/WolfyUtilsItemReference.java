package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.WolfyUtilCore;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

@ItemReferenceParserSettings(parser = WolfyUtilsItemReference.Parser.class)
@KeyedStaticId(key = "wolfyutils")
public class WolfyUtilsItemReference extends ItemReference {

    private static final org.bukkit.NamespacedKey CUSTOM_ITEM_KEY = new org.bukkit.NamespacedKey(WolfyCoreBukkit.getInstance(), "custom_item");

    private final NamespacedKey itemID;

    @JsonCreator
    public WolfyUtilsItemReference(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("itemID") NamespacedKey itemID) {
        //TODO: Inject WolfyCore instance!
        super(wolfyUtils);
        this.itemID = itemID;
    }

    protected WolfyUtilsItemReference(WolfyUtilsItemReference reference) {
        super(reference);
        this.itemID = reference.itemID;
    }

    @Override
    public WolfyUtilsItemReference copy() {
        return new WolfyUtilsItemReference(this);
    }

    @Override
    public ItemStack getItem() {
        var customItem = WolfyUtilCore.getInstance().getRegistries().getCustomItems().get(itemID);
        if (customItem != null) {
            return customItem.create();
        }
        WolfyUtilCore.getInstance().getLogger().warning("Couldn't find CustomItem for " + itemID.toString());
        return null;
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        if (itemStack != null) {
            var itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                var container = itemMeta.getPersistentDataContainer();
                if (container.has(CUSTOM_ITEM_KEY, PersistentDataType.STRING)) {
                    return Objects.equals(this.itemID, BukkitNamespacedKey.of(container.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING)));
                }
            }
        }
        return false;
    }

    public static class Parser implements ItemReference.Parser<WolfyUtilsItemReference> {

        @Override
        public Optional<WolfyUtilsItemReference> parseFromStack(WolfyUtils wolfyUtils, ItemStack stack) {
            if (stack != null) {
                var itemMeta = stack.getItemMeta();
                if (itemMeta != null) {
                    var container = itemMeta.getPersistentDataContainer();
                    if (container.has(CUSTOM_ITEM_KEY, PersistentDataType.STRING)) {
                        return Optional.of(new WolfyUtilsItemReference(wolfyUtils, BukkitNamespacedKey.of(container.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING))));
                    }
                }
            }
            return Optional.empty();
        }
    }
}
