package com.wolfyscript.utilities.bukkit.gui.animation;

import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.eval.context.EvalContextPlayer;
import com.wolfyscript.utilities.bukkit.gui.GUIHolder;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.gui.GuiHolder;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.RenderContext;
import com.wolfyscript.utilities.common.gui.animation.Animation;
import com.wolfyscript.utilities.common.gui.animation.ButtonAnimationFrame;
import com.wolfyscript.utilities.common.gui.components.Button;
import com.wolfyscript.utilities.common.items.ItemStackConfig;

public class ButtonAnimationFrameImpl implements ButtonAnimationFrame {

    private final Animation<ButtonAnimationFrame> animation;
    private final ItemStackConfig<?> stack;
    private final int duration;

    protected ButtonAnimationFrameImpl(Animation<ButtonAnimationFrame> animation, int duration, ItemStackConfig<?> stack) {
        this.animation = animation;
        this.duration = duration;
        this.stack = stack;
    }

    @Override
    public int duration() {
        return duration;
    }

    @Override
    public Animation<ButtonAnimationFrame> animation() {
        return animation;
    }

    @Override
    public ItemStackConfig<?> stack() {
        return stack;
    }

    @Override
    public void render(GuiViewManager guiViewManager, GuiHolder guiHolder, RenderContext renderContext) {
        renderContext.setNativeStack(renderContext.currentOffset() + animation.owner().position().slot(), ((BukkitItemStackConfig) stack).constructItemStack(
                new EvalContextPlayer(((GUIHolder) guiHolder).getBukkitPlayer()),
                WolfyCoreBukkit.getInstance().getWolfyUtils().getChat().getMiniMessage(),
                ((Button) animation.owner()).icon().getResolvers()
        ));
    }
}
