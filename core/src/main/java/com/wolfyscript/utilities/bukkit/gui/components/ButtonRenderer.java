package com.wolfyscript.utilities.bukkit.gui.components;

import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.eval.context.EvalContextPlayer;
import com.wolfyscript.utilities.bukkit.gui.BukkitNativeRenderer;
import com.wolfyscript.utilities.bukkit.gui.GUIHolder;
import com.wolfyscript.utilities.bukkit.gui.RenderContextImpl;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.Renderer;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.components.Button;
import java.util.HashMap;
import java.util.Map;

public class ButtonRenderer implements Renderer {

    private final Button button;

    public ButtonRenderer(Button button) {
        this.button = button;
    }

    @Override
    public int getWidth() {
        return button.width();
    }

    @Override
    public int getHeight() {
        return button.height();
    }

    @Override
    public void render(GuiHolder guiHolder, RenderContext context) {
        if (!(context instanceof RenderContextImpl renderContext)) return;
        renderContext.setNativeStack(renderContext.getCurrentOffset(),
                ((BukkitItemStackConfig) button.icon().getStack()).constructItemStack(
                        new EvalContextPlayer(((GUIHolder) guiHolder).getBukkitPlayer()),
                        WolfyCoreBukkit.getInstance().getWolfyUtils().getChat().getMiniMessage(),
                        button.icon().getResolvers()
                )
        );
    }

    @Override
    public Map<String, Signal<?>> getSignals() {
        return new HashMap<>();
    }

    @Override
    public BukkitNativeRenderer getNativeModule() {
        return new BukkitNativeRenderer();
    }
}
