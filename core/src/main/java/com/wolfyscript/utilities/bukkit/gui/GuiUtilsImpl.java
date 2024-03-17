package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.bukkit.gui.rendering.InventoryGUIRenderer;
import com.wolfyscript.utilities.gui.*;
import com.wolfyscript.utilities.gui.rendering.Renderer;
import com.wolfyscript.utilities.platform.gui.GuiUtils;

public class GuiUtilsImpl implements GuiUtils {

    @Override
    public Renderer<?> createRenderer(ViewRuntime viewRuntime) {
        return new InventoryGUIRenderer((ViewRuntimeImpl) viewRuntime);
    }
}
