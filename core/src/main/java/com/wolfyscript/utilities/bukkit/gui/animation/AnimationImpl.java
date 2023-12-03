package com.wolfyscript.utilities.bukkit.gui.animation;

import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.gui.RenderContextImpl;
import com.wolfyscript.utilities.common.gui.*;
import com.wolfyscript.utilities.common.gui.animation.AnimationCommonImpl;
import com.wolfyscript.utilities.common.gui.animation.AnimationFrame;
import com.wolfyscript.utilities.common.gui.animation.AnimationFrameBuilder;
import com.wolfyscript.utilities.common.gui.signal.Signal;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AnimationImpl<F extends AnimationFrame> extends AnimationCommonImpl<F> {

    AnimationImpl(Component owner, List<? extends AnimationFrameBuilder<F>> animationFrameBuilders, Signal<?> updateSignal) {
        super(owner, animationFrameBuilders, updateSignal);
        updateSignal.linkTo(this);
    }

    @Override
    public void update(GuiViewManager viewManager, GuiHolder guiHolder, RenderContext context) {
        if (!(context instanceof RenderContextImpl renderContext)) return;

        renderContext.enterNode(owner());

        AtomicInteger frameDelay = new AtomicInteger(0);
        AtomicInteger frameIndex = new AtomicInteger(0);
        Bukkit.getScheduler().runTaskTimer(((WolfyCoreBukkit) viewManager.getWolfyUtils().getCore()).getPlugin(), bukkitTask -> {
            int delay = frameDelay.getAndIncrement();
            int frame = frameIndex.get();
            if (frames().size() <= frame) {
                bukkitTask.cancel();
                if (owner() instanceof SignalledObject signalledOwner) {
                    signalledOwner.update(viewManager, guiHolder, context); // Last frame should be the original again!
                }
                return;
            }

            AnimationFrame frameObj = frames().get(frame);
            if (delay <= frameObj.duration()) {
                frameObj.render(viewManager, guiHolder, context);
                return;
            }
            frameIndex.incrementAndGet();
            frameDelay.set(0);
        }, 0, 1);
        renderContext.exitNode();
    }
}
