package com.wolfyscript.utilities.bukkit.items;

import com.wolfyscript.utilities.Copyable;

public class CustomBlockSettings implements Copyable<CustomBlockSettings> {

    private boolean useCustomDrops;

    public CustomBlockSettings() {
        this.useCustomDrops = true;
    }

    private CustomBlockSettings(CustomBlockSettings settings) {
        this.useCustomDrops = settings.useCustomDrops;
    }

    public boolean isUseCustomDrops() {
        return useCustomDrops;
    }

    public void setUseCustomDrops(boolean useCustomDrops) {
        this.useCustomDrops = useCustomDrops;
    }

    @Override
    public CustomBlockSettings copy() {
        return new CustomBlockSettings(this);
    }
}
