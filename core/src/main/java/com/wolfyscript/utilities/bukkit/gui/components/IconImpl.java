package com.wolfyscript.utilities.bukkit.gui.components;

import com.wolfyscript.utilities.bukkit.gui.GuiViewManagerImpl;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.components.Icon;

import com.wolfyscript.utilities.common.gui.impl.AbstractComponentImpl;
import it.unimi.dsi.fastutil.ints.IntList;
import org.bukkit.inventory.ItemStack;

public class IconImpl extends AbstractComponentImpl implements Icon<ItemStack> {

    private final BukkitItemStackConfig itemStackConfig;

    public IconImpl(WolfyUtils wolfyUtils, String id, Component parent, BukkitItemStackConfig itemStackConfig, int[] slots) {
        super(id, wolfyUtils, parent, IntList.of(slots));
        this.itemStackConfig = itemStackConfig;
    }

    @Override
    public Icon<ItemStack> construct(GuiHolder holder, GuiViewManager guiViewManager) {
        return this;
    }

    @Override
    public void remove(GuiHolder guiHolder, GuiViewManager guiViewManager, RenderContext renderContext) {
        for (int slot : getSlots()) {
            renderContext.setNativeStack(slot, null);
            ((GuiViewManagerImpl) guiHolder.getViewManager()).updateLeaveNodes(null, slot);
        }
    }

    @Override
    public int width() {
        return 1;
    }

    @Override
    public int height() {
        return 1;
    }

    @Override
    public BukkitItemStackConfig getItemStackConfig() {
        return itemStackConfig;
    }

    @Override
    public void update(GuiViewManager viewManager, GuiHolder guiHolder, RenderContext renderContext) {

    }
}
