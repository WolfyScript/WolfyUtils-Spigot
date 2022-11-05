package com.wolfyscript.utilities.bukkit.items.reference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

@ItemReferenceParserSettings
public class WolfyUtilsItemReference extends ItemReference {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("wolfyutils");
    private static final org.bukkit.NamespacedKey CUSTOM_ITEM_KEY = new org.bukkit.NamespacedKey(WolfyUtilities.getWUPlugin(), "custom_item");

    private final NamespacedKey itemID;

    @JsonCreator
    public WolfyUtilsItemReference(@JsonProperty("itemID") NamespacedKey itemID) {
        //TODO: Inject WolfyCore instance!
        super(ID);
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
        WolfyUtilities.getWUCore().getConsole().warn("Couldn't find CustomItem for " + itemID.toString());
        return null;
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        if (itemStack != null) {
            var itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                var container = itemMeta.getPersistentDataContainer();
                if (container.has(CUSTOM_ITEM_KEY, PersistentDataType.STRING)) {
                    return Objects.equals(this.itemID, NamespacedKey.of(container.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING)));
                }
            }
        }
        return false;
    }

    public static WolfyUtilsItemReference parseFromStack(ItemStack itemStack) {
        if (itemStack == null) return null;
        var itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            var container = itemMeta.getPersistentDataContainer();
            if (container.has(CUSTOM_ITEM_KEY, PersistentDataType.STRING)) {
                return new WolfyUtilsItemReference(NamespacedKey.of(container.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING)));
            }
        }
        return null;
    }
}
