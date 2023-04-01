package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.items.ItemStackConfig;
import java.util.ArrayDeque;
import java.util.Deque;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RenderContextImpl implements RenderContext {

    private final Inventory inventory;
    private ComponentState currentNode;
    private int slotOffsetToParent;
    private final Deque<Component> componentPath = new ArrayDeque<>();

    public RenderContextImpl(Inventory inventory) {
        this.inventory = inventory;
        this.slotOffsetToParent = 0;
        this.currentNode = null;
    }

    void pushParentOnPath(Component component) {
        componentPath.push(component);
    }

    void setSlotOffsetToParent(int offset) {
        this.slotOffsetToParent = offset;
    }

    int getSlotOffsetToParent() {
        return slotOffsetToParent;
    }

    void setCurrentNode(ComponentState node) {
        this.currentNode = node;
    }

    Component nextChild() {
        return componentPath.pop();
    }

    Inventory getInventory() {
        return inventory;
    }

    @Override
    public ComponentState getCurrentState() {
        return currentNode;
    }

    @Override
    public void setStack(int i, ItemStackConfig<?> itemStackConfig) {
        // i > 0 && i < width * height
        if (itemStackConfig instanceof BukkitItemStackConfig bukkitItemStackConfig) {
            if (checkIfSlotInBounds(i)) {
                inventory.setItem(i, bukkitItemStackConfig.constructItemStack());
            }
        }
    }

    public void setNativeStack(int i, ItemStack itemStack) {
        if (checkIfSlotInBounds(i)) inventory.setItem(i, itemStack);
    }

    @Override
    public Component next() {
        return null;
    }

}
