package com.wolfyscript.utilities.bukkit.world.items;

import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.chat.ChatColor;
import com.wolfyscript.utilities.bukkit.world.inventory.item_builder.ItemBuilder;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemReference;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.List;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class Items {

    private final WolfyUtils wolfyUtils;

    public Items(WolfyUtils wolfyUtils) {
        this.wolfyUtils = wolfyUtils;
    }

    /**
     * Get the CustomItem via ItemStack.
     * It checks for the PersistentData containing the NamespacedKey of WolfyUtilities.
     * When that isn't found it checks for ItemsAdder and Oraxen values saved in the Items NBT.
     *
     * @param itemStack the ItemStack to check
     * @return the CustomItem linked to the specific API this Item is from.
     */
    public CustomItem getWithReferenceTo(ItemStack itemStack) {
        if (itemStack != null) {
            ItemReference reference = ((WolfyUtilsBukkit)wolfyUtils).getRegistries().getItemReferences().parse(itemStack);
            if (reference != null) {
                reference.setAmount(itemStack.getAmount());
                return new CustomItem(wolfyUtils, reference);
            }
            return new CustomItem(wolfyUtils, itemStack);
        }
        return null;
    }

    /*
        Prepare and configure the ItemStack for the GUI!
         */
    public ItemStack createItem(ItemStack itemStack, String displayName, String... lore) {
        var itemBuilder = new ItemBuilder(wolfyUtils, itemStack);
        var itemMeta = itemBuilder.getItemMeta();
        if (itemMeta != null) {
            itemBuilder.setDisplayName(ChatColor.convert(displayName));
            if (lore != null) {
                for (String s : lore) {
                    itemBuilder.addLoreLine(ChatColor.convert(s));
                }
            }
            itemBuilder.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS);
        }
        return itemBuilder.create();
    }

    public ItemStack createItem(ItemStack itemStack, Component displayName, List<Component> lore) {
        var itemBuilder = new ItemBuilder(wolfyUtils, itemStack);
        var itemMeta = itemBuilder.getItemMeta();
        if (itemMeta != null) {
            itemBuilder.displayName(displayName);
            itemBuilder.lore(lore);
            itemBuilder.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS);
        }
        return itemBuilder.create();
    }

}
