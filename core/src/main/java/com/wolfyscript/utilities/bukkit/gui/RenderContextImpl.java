package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.Router;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.Signalable;
import com.wolfyscript.utilities.common.gui.Window;
import com.wolfyscript.utilities.common.items.ItemStackConfig;
import java.util.ArrayDeque;
import java.util.Deque;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RenderContextImpl implements RenderContext {

    private final Inventory inventory;
    private final Window window;
    private final Router router;
    private ComponentState currentNode;
    private int slotOffsetToParent;
    private final Deque<Component> componentPath = new ArrayDeque<>();

    public RenderContextImpl(Inventory inventory, Router router, Window window) {
        this.inventory = inventory;
        this.router = router;
        this.window = window;
        this.slotOffsetToParent = 0;
        this.currentNode = null;
    }

    void setSlotOffsetToParent(int offset) {
        this.slotOffsetToParent = offset;
    }

    @Override
    public int getCurrentOffset() {
        return slotOffsetToParent;
    }

    void enterNode(GuiViewManager viewManager, ComponentState state) {
        this.currentNode = state;
        if (currentNode instanceof Signalable signalable) {
            for (Signal<?> signal : signalable.getSignalValues().values()) {
                signal.enter(viewManager);
            }
        }
    }

    void exitNode() {
        if (currentNode instanceof Signalable signalable) {
            for (Signal<?> signal : signalable.getSignalValues().values()) {
                if (signal.exit()) {
                    ((Signalable) currentNode).receiveUpdate(signal);
                }
            }
        }
        this.currentNode = null;
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
        checkIfSlotInBounds(i);
        if (!(itemStackConfig instanceof BukkitItemStackConfig bukkitItemStackConfig))
            throw new IllegalArgumentException(String.format("Cannot render stack config! Invalid stack config type! Expected '%s' but received '%s'.", ItemStack.class.getName(), itemStackConfig.getClass().getName()));

        inventory.setItem(i, bukkitItemStackConfig.constructItemStack());
    }

    @Override
    public void setNativeStack(int i, Object object) {
        checkIfSlotInBounds(i);
        if (!(object instanceof ItemStack itemStack))
            throw new IllegalArgumentException(String.format("Cannot render native stack! Invalid native stack type! Expected '%s' but received '%s'.", ItemStack.class.getName(), object.getClass().getName()));

        inventory.setItem(i, itemStack);
    }

}
