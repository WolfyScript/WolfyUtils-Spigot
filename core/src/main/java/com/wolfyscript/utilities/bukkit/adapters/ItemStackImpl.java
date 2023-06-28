package com.wolfyscript.utilities.bukkit.adapters;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.adapters.ItemStack;
import com.wolfyscript.utilities.common.items.ItemStackConfig;

public class ItemStackImpl extends BukkitRefAdapter<org.bukkit.inventory.ItemStack> implements ItemStack {

    private final WolfyUtils wolfyUtils;

    public ItemStackImpl(WolfyUtilsBukkit wolfyUtils, org.bukkit.inventory.ItemStack bukkitRef) {
        super(bukkitRef);
        this.wolfyUtils = wolfyUtils;
    }

    @Override
    public NamespacedKey getItem() {
        return getBukkitRef() == null ? null : BukkitNamespacedKey.fromBukkit(getBukkitRef().getType().getKey());
    }

    @Override
    public int getAmount() {
        return getBukkitRef().getAmount();
    }

    @Override
    public ItemStackConfig<?> snapshot() {
        return new BukkitItemStackConfig(wolfyUtils, getBukkitRef());
    }
}
