package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.common.gui.Data;
import com.wolfyscript.utilities.common.gui.RenderContext;
import org.bukkit.inventory.Inventory;

public class RenderContextImpl<D extends Data> implements RenderContext<D> {

    private Inventory inventory;

    public RenderContextImpl(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public void setChildComponent(int i, String s) {

    }

    @Override
    public void setRootChildComponent(int i, String... strings) {

    }
}
