package com.wolfyscript.utilities.bukkit.world.items;

import com.wolfyscript.utilities.WolfyCore;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.bukkit.data.SpigotDataKeyBuilderProvider;
import com.wolfyscript.utilities.data.DataKeyBuilderProvider;
import com.wolfyscript.utilities.platform.world.items.Items;
import com.wolfyscript.utilities.world.items.ItemStackConfig;

public class ItemsImpl implements Items {

    private final WolfyCore wolfyCore;
    private final SpigotDataKeyBuilderProvider dataKeyBuilderFactory;

    public ItemsImpl(WolfyCore wolfyCore) {
        this.wolfyCore = wolfyCore;
        this.dataKeyBuilderFactory = new SpigotDataKeyBuilderProvider(wolfyCore);
    }

    @Override
    public ItemStackConfig createStackConfig(WolfyUtils wolfyUtils, String s) {
        return new BukkitItemStackConfig(wolfyUtils, s);
    }

    @Override
    public DataKeyBuilderProvider dataKeyProvider() {
        return dataKeyBuilderFactory;
    }

}
