package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.SizedComponent;
import com.wolfyscript.utilities.common.items.ItemStackConfig;
import org.bukkit.inventory.Inventory;

public class RenderContextImpl<S extends ComponentState> implements RenderContext {

    private final Inventory inventory;
    private S currentNode;

    public RenderContextImpl(Inventory inventory) {
        this.inventory = inventory;
        this.currentNode = null;
    }

    void setCurrentNode(S node) {
        this.currentNode = node;
    }

    @Override
    public S getCurrentState() {
        return currentNode;
    }

    @Override
    public void setStack(int i, ItemStackConfig<?> itemStackConfig) {
        // i > 0 && i < width * height
        if (itemStackConfig instanceof BukkitItemStackConfig bukkitItemStackConfig) {
            if (!(currentNode.getOwner() instanceof SizedComponent sizedParent) || i > 0 && i < sizedParent.width() * sizedParent.height()) {
                inventory.setItem(i, bukkitItemStackConfig.constructItemStack());
            } else {
                throw new IllegalArgumentException("Slot "+ i +" out of bounds! Must be in the range of [" + 0 + "..." + (sizedParent.width() * sizedParent.height() - 1) + "] !");
            }

        }
    }

}
