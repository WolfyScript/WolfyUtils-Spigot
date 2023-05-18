package com.wolfyscript.utilities.bukkit.gui.components;

import com.wolfyscript.utilities.bukkit.gui.AbstractBukkitComponent;
import com.wolfyscript.utilities.bukkit.gui.ComponentStateImpl;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.ComponentState;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.components.Icon;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public class IconImpl extends AbstractBukkitComponent implements Icon<ItemStack> {

    private final BukkitItemStackConfig itemStackConfig;

    public IconImpl(WolfyUtils wolfyUtils, String id, Component parent, BukkitItemStackConfig itemStackConfig) {
        super(id, wolfyUtils, parent);
        this.itemStackConfig = itemStackConfig;
    }

    @Override
    public void init() {

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
    public ComponentState createState(ComponentState componentState, GuiViewManager guiViewManager) {
        return new ComponentStateImpl<Icon<ItemStack>, ComponentState>(componentState, this) {

            @Override
            public void render(GuiHolder holder, RenderContext context) {
                context.setStack(context.getCurrentOffset(), getOwner().getItemStackConfig());
            }
        };
    }

    @Override
    public Map<String, Signal<?>> signals() {
        return null;
    }
}
