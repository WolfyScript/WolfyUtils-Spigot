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
        manager.registerRouter("counter", builder -> builder
                .children(children -> children
                        .window("mainmenu", mainMenu -> mainMenu
                                .title((guiHolder, window, state) -> Component.text("Main Menu"))
                                .size(27)
                                // Creates the signal this component will track and children can listen to
                                .createSignal(COUNT, Integer.class, count -> count.defaultValue(state -> 0))
                                .render((holder, rendering) -> {
                                    Signal.Value<Integer> count = rendering.createSignal(COUNT, Integer.class, () -> 0);

                                    rendering
                                            // The state of a component is only reconstructed if the slot it is positioned at changes.

                                            // Here the slot will always have the same type of component, so the state is created only once.
                                            // Static rendering, slots do not change
                                            .component(4, "count_up")
                                            .component(13, "counter")
                                            .component(22, "count_down")
                                            .component(10, "reset")
                                            // Dynamic rendering, Replaces the static rendering!
                                            .component("count_up")
                                            .component("counter")

                                            // Reactive parts are called everytime the signal is updated.
                                            .reactive(count, signal -> {
                                                if (signal.get() > 0) {
                                                    // These components may be cleared when count == 0, so the state is recreated whenever the count changes from 0 to >0.
                                                    rendering.component("count_down")
                                                            .component("reset");
                                                }
                                            });
                                })
                                // The children that can be used inside of this window.
                                .children(mainMenuChildren -> mainMenuChildren
                                        .button("counter", settingsBtn -> settingsBtn
                                                .icon(icon -> icon
                                                        .stack(() -> {
                                                            BukkitItemStackConfig config = new BukkitItemStackConfig(core.getWolfyUtils(), "minecraft:redstone");
                                                            config.setName(new ValueProviderStringConst(core.getWolfyUtils(), "<!italic>Clicked <b><signal:count:1></b> times!"));
                                                            return config;
                                                        })
                                                        .dynamic()
                                                )
                                        )
                                        .button("count_up", settingsBtn -> settingsBtn
                                                .icon(icon -> icon
                                                        .stack(() -> {
                                                            BukkitItemStackConfig config = new BukkitItemStackConfig(core.getWolfyUtils(), "minecraft:green_concrete");
                                                            config.setName(new ValueProviderStringConst(core.getWolfyUtils(), "<green><b>Count Up"));
                                                            return config;
                                                        })
                                                        .dynamic()
                                                )
                                                .interact((guiHolder, componentState, interactionDetails) -> {
                                                    componentState.getParent().captureSignal(COUNT, Integer.class).update(integer -> ++integer);
                                                    return InteractionResult.cancel(true);
                                                })
                                        )
                                        .button("reset", resetBtn -> resetBtn
                                                .icon(icon -> icon.stack(() -> {
                                                    BukkitItemStackConfig config = new BukkitItemStackConfig(core.getWolfyUtils(), "minecraft:tnt");
                                                    config.setName(new ValueProviderStringConst(core.getWolfyUtils(), "<b><red>Reset Clicks!"));
                                                    return config;
                                                }))
                                                .interact((guiHolder, componentState, interactionDetails) -> {
                                                    // Children can capture the signals of their parents, but not the other way around.
                                                    Signal.Value<Integer> count = componentState.getParent().captureSignal(COUNT, Integer.class);
                                                    count.set(0); // The set method changes the value of the signal and prompts the listener of the signal to re-render.
                                                    return InteractionResult.cancel(true);
                                                })
                                        )
                                        // Instead of using the pre-made builders you can use custom builders
                                        .custom("count_down", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                .icon(icon -> icon
                                                        .stack(() -> {
                                                            BukkitItemStackConfig config = new BukkitItemStackConfig(core.getWolfyUtils(), "minecraft:red_concrete");
                                                            config.setName(new ValueProviderStringConst(core.getWolfyUtils(), "<red><b>Count Down"));
                                                            return config;
                                                        })
                                                        .dynamic()
                                                )
                                                .interact((guiHolder, componentState, interactionDetails) -> {
                                                    componentState.getParent().captureSignal(COUNT, Integer.class).update(integer -> --integer);
                                                    return InteractionResult.cancel(true);
                                                })
                                        )
                                )
                        )
                )
        );
    }

    public void initWithConfig() {
        GuiAPIManager manager = core.getWolfyUtils().getGUIManager();
        final String COUNT = "count";
        manager.registerRouterFromFile(new File(core.getWolfyUtils().getDataFolder().getPath(), "com/wolfyscript/utilities/common/gui/counter/counter_router.conf"), builder -> builder
                .children(children -> children
                        .window("main_menu", mainMenu -> mainMenu
                                // Creates the signal this component will track and children can listen to
                                .createSignal(COUNT, Integer.class, count -> count.defaultValue(state -> 0))
                                .render((holder, rendering) -> {
                                    Signal.Value<Integer> count = rendering.createSignal(COUNT, Integer.class, () -> 0);

                                    rendering
                                            // The state of a component is only reconstructed if the slot it is positioned at changes.
                                            // Here the slot will always have the same type of component, so the state is created only once.
                                            .component("count_up")
                                            .component("counter")
                                            // Reactive parts are called everytime the signal is updated.
                                            .reactive(count, signal -> {
                                                if (signal.get() > 0) {
                                                    // These components may be cleared when count == 0, so the state is recreated whenever the count changes from 0 to >0.
                                                    rendering.component("count_down")
                                                            .component("reset");
                                                }
                                            });
                                })
                                // The children that can be used inside of this window.
                                .children(mainMenuChildren -> mainMenuChildren
                                        .button("count_up", settingsBtn -> settingsBtn
                                                .interact((guiHolder, componentState, interactionDetails) -> {
                                                    componentState.getParent().captureSignal(COUNT, Integer.class).update(integer -> ++integer);
                                                    return InteractionResult.cancel(true);
                                                })
                                        )
                                        .button("reset", resetBtn -> resetBtn
                                                .interact((guiHolder, componentState, interactionDetails) -> {
                                                    // Children can capture the signals of their parents, but not the other way around.
                                                    Signal.Value<Integer> count = componentState.getParent().captureSignal(COUNT, Integer.class);
                                                    count.set(0); // The set method changes the value of the signal and prompts the listener of the signal to re-render.
                                                    return InteractionResult.cancel(true);
                                                })
                                        )
                                        // Instead of using the pre-made builders you can use custom builders
                                        .custom("count_down", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                .interact((guiHolder, componentState, interactionDetails) -> {
                                                    componentState.getParent().captureSignal(COUNT, Integer.class).update(integer -> --integer);
                                                    return InteractionResult.cancel(true);
                                                })
                                        )
                                )
                        )
                )
        );
    }


}
