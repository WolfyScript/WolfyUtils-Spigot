package com.wolfyscript.utilities.bukkit.gui.animation;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.gui.animation.Animation;
import com.wolfyscript.utilities.common.gui.animation.ButtonAnimationFrame;
import com.wolfyscript.utilities.common.gui.animation.ButtonAnimationFrameBuilder;
import com.wolfyscript.utilities.common.items.ItemStackConfig;

public class ButtonAnimationFrameBuilderImpl implements ButtonAnimationFrameBuilder {

    private int duration;
    private ItemStackConfig<?> stack;

    public ButtonAnimationFrameBuilderImpl() {
        this.duration = 1;
    }

    @Override
    public ButtonAnimationFrameBuilder stack(ItemStackConfig<?> itemStackConfig) {
        Preconditions.checkArgument(itemStackConfig instanceof BukkitItemStackConfig, "The stack config of the AnimationFrame must be a BukkitItemStackConfig!");
        this.stack = itemStackConfig;
        return this;
    }

    @Override
    public ButtonAnimationFrameBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public ButtonAnimationFrame build(Animation<ButtonAnimationFrame> animation) {
        return new ButtonAnimationFrameImpl(animation, duration, stack);
    }


}
