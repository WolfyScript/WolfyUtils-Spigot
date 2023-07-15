package com.wolfyscript.utilities.bukkit.gui.components;

import com.wolfyscript.utilities.bukkit.gui.AbstractBukkitComponent;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.Renderer;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.components.Icon;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.IntList;
import org.bukkit.inventory.ItemStack;

public class IconImpl extends AbstractBukkitComponent implements Icon<ItemStack> {

    private final BukkitItemStackConfig itemStackConfig;

    public IconImpl(WolfyUtils wolfyUtils, String id, Component parent, BukkitItemStackConfig itemStackConfig, int[] slots) {
        super(id, wolfyUtils, parent, IntList.of(slots));
        this.itemStackConfig = itemStackConfig;
    }

    @Override
    public Renderer getRenderer() {
        return null;
    }

    @Override
    public Renderer construct(GuiViewManager guiViewManager) {
        return null;
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
