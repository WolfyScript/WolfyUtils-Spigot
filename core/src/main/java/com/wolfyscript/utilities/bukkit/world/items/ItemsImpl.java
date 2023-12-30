package com.wolfyscript.utilities.bukkit.world.items;

import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.platform.world.items.Items;
import com.wolfyscript.utilities.world.items.ItemStackConfig;

public class ItemsImpl implements Items {

    @Override
    public ItemStackConfig createStackConfig(WolfyUtils wolfyUtils, String s) {
        return new BukkitItemStackConfig(wolfyUtils, s);
    }
}
