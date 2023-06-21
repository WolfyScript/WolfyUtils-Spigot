package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.bukkit.WolfyCoreImpl;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.gui.GuiAPIManager;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.components.ButtonBuilder;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderStringConst;
import java.io.File;
import net.kyori.adventure.text.Component;

public class TestGUI {

    private WolfyCoreImpl core;

    public TestGUI(WolfyCoreImpl core) {
        this.core = core;
    }

    public void initCodeOnly() {
        GuiAPIManager manager = core.getWolfyUtils().getGUIManager();
        final String COUNT = "count";
        manager.registerGui("counter", builder -> builder
                .window(mainMenu -> mainMenu
                        .size(27)
                        // Creates the signal this component will track and children can listen to
                        .render(rendering -> {
                            Signal<Integer> count = rendering.useSignal(COUNT, Integer.class, () -> 0);

                            rendering
                                    .title(() -> Component.text("Main Menu"))
                                    // The state of a component is only reconstructed if the slot it is positioned at changes.

                                    // Here the slot will always have the same type of component, so the state is created only once.
                                    // Static Rendering, uses the positions specified previously!
                                    .renderAt(4, "count_up", ButtonBuilder.class, settingsBtn -> settingsBtn
                                            .icon(icon -> icon
                                                    .stack(() -> {
                                                        BukkitItemStackConfig config = new BukkitItemStackConfig(core.getWolfyUtils(), "minecraft:green_concrete");
                                                        config.setName(new ValueProviderStringConst(core.getWolfyUtils(), "<green><b>Count Up"));
                                                        return config;
                                                    })
                                            )
                                            .interact((guiHolder, componentState, interactionDetails) -> {
                                                count.update(integer -> ++integer);
                                                return InteractionResult.cancel(true);
                                            }))
                                    .renderAt(13, "counter", ButtonBuilder.class, settingsBtn -> settingsBtn
                                            .icon(icon -> icon
                                                    .stack(() -> {
                                                        BukkitItemStackConfig config = new BukkitItemStackConfig(core.getWolfyUtils(), "minecraft:redstone");
                                                        config.setName(new ValueProviderStringConst(core.getWolfyUtils(), "<!italic>Clicked <b><count></b> times!"));
                                                        return config;
                                                    })
                                                    .updateOnSignals(count)
                                            )
                                    )
                                    // Reactive parts are called everytime the signal is updated.
                                    .reactive(reactiveBuilder -> {
                                        if (count.get() > 0) {
                                            // These components may be cleared when count == 0, so the state is recreated whenever the count changes from 0 to >0.
                                            reactiveBuilder
                                                    .renderAt(22, "count_down", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                            .icon(icon -> icon
                                                                    .stack(() -> {
                                                                        BukkitItemStackConfig config = new BukkitItemStackConfig(core.getWolfyUtils(), "minecraft:red_concrete");
                                                                        config.setName(new ValueProviderStringConst(core.getWolfyUtils(), "<red><b>Count Down"));
                                                                        return config;
                                                                    })
                                                            )
                                                            .interact((guiHolder, componentState, interactionDetails) -> {
                                                                count.update(integer -> --integer);
                                                                return InteractionResult.cancel(true);
                                                            }))
                                                    .renderAt(10, "reset", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                            .icon(icon -> icon.stack(() -> {
                                                                BukkitItemStackConfig config = new BukkitItemStackConfig(core.getWolfyUtils(), "minecraft:tnt");
                                                                config.setName(new ValueProviderStringConst(core.getWolfyUtils(), "<b><red>Reset Clicks!"));
                                                                return config;
                                                            }))
                                                            .interact((guiHolder, componentState, interactionDetails) -> {
                                                                count.set(0); // The set method changes the value of the signal and prompts the listener of the signal to re-render.
                                                                return InteractionResult.cancel(true);
                                                            }));
                                        }
                                    });
                        })
                )
        );
    }

    public void initWithConfig() {
        GuiAPIManager manager = core.getWolfyUtils().getGUIManager();
        final String COUNT = "count";
        manager.registerGuiFromFiles("example_counter", builder -> builder
                .window(mainMenu -> mainMenu
                        .size(9 * 3)
                        .render((renderer) -> {
                            // This is only called upon creation of the state. So this is not called when the signal is updated!
                            Signal<Integer> count = renderer.useSignal(COUNT, Integer.class, () -> 0);

                            renderer
                                    .titleSignals(count)
                                    // Sometimes we want to render components dependent on signals
                                    .reactive(reactiveBuilder -> {
                                        // Reactive parts are called everytime the signal used inside this closure is updated.
                                        if (count.get() > 0) {
                                            // These components may be cleared when count == 0, so the state is recreated whenever the count changes from 0 to >0.
                                            reactiveBuilder
                                                    .render("count_down", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                            .interact((guiHolder, componentState, interactionDetails) -> {
                                                                count.update(old -> --old);
                                                                return InteractionResult.cancel(true);
                                                            })
                                                    ).render("reset", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                            .interact((guiHolder, componentState, interactionDetails) -> {
                                                                count.set(0); // The set method changes the value of the signal and prompts the listener of the signal to re-render.
                                                                return InteractionResult.cancel(true);
                                                            })
                                                    );
                                        }
                                    })
                                    // The state of a component is only reconstructed if the slot it is positioned at changes.
                                    // Here the slot will always have the same type of component, so the state is created only once.
                                    .render("count_up", ButtonBuilder.class, countUpSettings -> countUpSettings
                                            .interact((guiHolder, componentState, interactionDetails) -> {
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
