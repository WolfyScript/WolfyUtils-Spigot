package com.wolfyscript.utilities.bukkit.gui.animation;

import com.wolfyscript.utilities.common.gui.Component;
import com.wolfyscript.utilities.common.gui.DynamicConstructor;
import com.wolfyscript.utilities.common.gui.animation.Animation;
import com.wolfyscript.utilities.common.gui.animation.AnimationBuilderCommonImpl;
import com.wolfyscript.utilities.common.gui.animation.AnimationFrame;
import com.wolfyscript.utilities.common.gui.animation.AnimationFrameBuilder;

import java.util.function.Supplier;

public class AnimationBuilderImpl<F extends AnimationFrame, FB extends AnimationFrameBuilder<F>> extends AnimationBuilderCommonImpl<F, FB> {

    public AnimationBuilderImpl(DynamicConstructor dynamicConstructor, Supplier<FB> frameBuilderSupplier) {
        super(dynamicConstructor, frameBuilderSupplier);
    }

    @Override
    public Animation<F> build(Component component) {
        if (updateSignal == null) {
            updateSignal = dynamicConstructor.signal(component.getID() + "_click_animation_handler", Boolean.TYPE, () -> false);
        }

        return new AnimationImpl<>(component, frameBuilders, updateSignal);
    }
}
