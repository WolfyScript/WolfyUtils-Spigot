package com.wolfyscript.utilities.bukkit.world.items.enchanting;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.world.items.enchanting.Enchantment;

public class EnchantmentImpl implements Enchantment {

    private final org.bukkit.enchantments.Enchantment bukkit;

    public EnchantmentImpl(org.bukkit.enchantments.Enchantment enchantment) {
        this.bukkit = enchantment;
    }

    @Override
    public int maxLevel() {
        return bukkit.getMaxLevel();
    }

    @Override
    public int minLevel() {
        return bukkit.getStartLevel();
    }

    @Override
    public NamespacedKey key() {
        return BukkitNamespacedKey.fromBukkit(bukkit.getKey());
    }
}
