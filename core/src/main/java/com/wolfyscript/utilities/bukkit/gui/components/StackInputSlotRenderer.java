package com.wolfyscript.utilities.bukkit.gui.components;

import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl;
import com.wolfyscript.utilities.bukkit.gui.BukkitNativeRenderer;
import com.wolfyscript.utilities.bukkit.gui.RenderContextImpl;
import com.wolfyscript.utilities.common.gui.*;
import com.wolfyscript.utilities.common.gui.components.StackInputSlot;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class StackInputSlotRenderer implements Renderer<ComponentState> {

    private final StackInputSlot stackInputSlot;

    public StackInputSlotRenderer(StackInputSlot stackInputSlot) {
        this.stackInputSlot = stackInputSlot;
    }

    @Override
    public int getWidth() {
        return stackInputSlot.width();
    }

    @Override
    public int getHeight() {
        return stackInputSlot.height();
    }

    @Override
    public void render(ComponentState state, GuiHolder guiHolder, RenderContext context) {
        if (!(context instanceof RenderContextImpl renderContext)) return;
        ItemStackImpl value = (ItemStackImpl) stackInputSlot.signal().get();
        renderContext.setNativeStack(renderContext.getCurrentOffset(), value != null ? value.getBukkitRef() : new ItemStack(Material.AIR));
    }

    @Override
    public void renderComponent(ComponentState state, int i, Component component) { }

    @Override
    public Map<String, Signal<?>> getSignals() {
        return new HashMap<>();
    }

    @Override
    public BukkitNativeRenderer getNativeModule() {
        return new BukkitNativeRenderer();
    }

}
