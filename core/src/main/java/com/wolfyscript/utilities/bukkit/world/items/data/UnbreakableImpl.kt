package com.wolfyscript.utilities.bukkit.world.items.data;

import com.wolfyscript.utilities.world.items.data.Unbreakable;

public class UnbreakableImpl implements Unbreakable {

    private final boolean showInTooltip;

    public UnbreakableImpl(boolean showInTooltip) {
        this.showInTooltip = showInTooltip;
    }

    @Override
    public boolean showInTooltip() {
        return showInTooltip;
    }
}
