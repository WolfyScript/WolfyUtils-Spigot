package com.wolfyscript.utilities.bukkit.gui.example;

import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.common.gui.GuiAPIManager;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.components.ButtonBuilder;
import com.wolfyscript.utilities.common.gui.signal.Signal;
import com.wolfyscript.utilities.common.gui.signal.Store;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

public class CounterExample {

    private static final Map<GuiViewManager, CounterStore> counterStores = new WeakHashMap<>();

    /**
     * Stores the count value so that it persists when the GUI is closed.
     */
    private static class CounterStore {

        private int count = 0;

        public void setCount(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

    static void register(GuiAPIManager manager) {
        manager.registerGuiFromFiles("example_counter", builder -> builder
                .window(mainMenu -> mainMenu
                        .size(9 * 3)
                        .construct((renderer) -> {
                            // This is only called upon creation of the component. So this is not called when the signal is updated!

                            // Use signals that provide a simple value storage & synchronisation. Signals are not persistent and will get destroyed when the GUI is closed!
                            Signal<Integer> countSignal = renderer.signal("count_signal", Integer.class, () -> 0);

                            // Optionally, sync your data with the gui using custom data stores. This makes it possible to store persistent data.
                            CounterStore counterStore = counterStores.computeIfAbsent(renderer.viewManager(), guiViewManager -> new CounterStore());
                            Store<Integer> count = renderer.syncStore("count", Integer.class, counterStore::getCount, counterStore::setCount);

                            renderer
                                    .titleSignals(count)
                                    .render("count_down", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                            .interact((guiHolder, interactionDetails) -> {
                                                count.update(old -> --old);
                                                return InteractionResult.cancel(true);
                                            })
                                    )
                                    // Sometimes we want to render components dependent on signals
                                    .ifThenRender(() -> count.get() != 0, "reset", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                            .interact((guiHolder, interactionDetails) -> {
                                                count.set(0); // The set method changes the value of the signal and prompts the listener of the signal to re-render.
                                                return InteractionResult.cancel(true);
                                            })
                                            .sound(holder -> Optional.of(Sound.sound(Key.key("minecraft:entity.dragon_fireball.explode"), Sound.Source.MASTER, 0.25f, 1)))
                                    )
                                    // The state of a component is only reconstructed if the slot it is positioned at changes.
                                    // Here the slot will always have the same type of component, so the state is created only once.
                                    .render("count_up", ButtonBuilder.class, countUpSettings -> countUpSettings
                                            .interact((guiHolder, interactionDetails) -> {
                                                count.update(old -> ++old);
                                                return InteractionResult.cancel(true);
                                            })
                                    )
                                    .render("counter", ButtonBuilder.class, bb -> bb.icon(ib -> ib.updateOnSignals(count)));
                        })
                )
        );
    }




}
