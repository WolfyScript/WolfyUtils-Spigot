package com.wolfyscript.utilities.bukkit.items;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.common.items.ItemStackConfig;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BukkitItemStackConfig extends ItemStackConfig<ItemStack> {

    @JsonCreator
    public BukkitItemStackConfig(@JsonProperty("itemId") String itemId) {
        super(itemId);
    }

    public BukkitItemStackConfig(ItemStack stack) {
        super(stack.getType().getKey().toString());

        this.amount = stack.getAmount();
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            this.lore = meta.getLore();
            this.unbreakable = meta.isUnbreakable();
            this.customModelData = meta.getCustomModelData();
        }
        this.enchants = stack.getEnchantments().entrySet().stream().collect(Collectors.toMap(entry-> entry.getKey().getKey().toString(), Map.Entry::getValue));

    }

    @Override
    public ItemStack constructItemStack() {
        Material type = Material.matchMaterial(itemId);
        if (type != null) {
            ItemStack itemStack = new ItemStack(type);
            itemStack.setAmount(amount);

            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                //TODO: Adventure format
                meta.setDisplayName(name);
                meta.setLore(lore);

                for (Map.Entry<String, Integer> entry : enchants.entrySet()) {
                    Enchantment enchant = Enchantment.getByKey(NamespacedKey.fromString(entry.getKey()));
                    if (enchant != null) {
                        meta.addEnchant(enchant, entry.getValue(), true);
                    }
                }

                meta.setCustomModelData(customModelData);
                meta.setUnbreakable(unbreakable);

                itemStack.setItemMeta(meta);
            }
            return itemStack;
        }
        return null;
    }
}
