package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.gui.ButtonBuilder;
import com.wolfyscript.utilities.common.gui.GuiAPIManager;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderStringConst;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TestGUI {

    private WolfyCoreBukkit core;

    public TestGUI(WolfyCoreBukkit core) {
        this.core = core;
    }

    public void init() {
        GuiAPIManager manager = core.getWolfyUtils().getGUIManager();

        MiniMessage miniMessage = core.getWolfyUtils().getChat().getMiniMessage();

        final String COUNT = "count";

        manager.registerRouter("counter", builder -> builder
                .children(children -> children
                        .window("mainmenu", mainMenu -> mainMenu
                                .title((guiHolder, window) -> Component.text("Main Menu"))
                                .size(27)
                                // Specifies/Creates the signal this component will track
                                .useSignal(COUNT, Integer.class, count -> count.defaultValue(state -> 0))
                                // Renders the child components into the inventory GUI. Called everytime the signal has been updated.
                                .render((guiHolder, state) -> {
                                    // The state of a component is only reconstructed if the slot changes
                                    Signal.Value<Integer> count = state.captureSignal(COUNT, Integer.class);
                                    // Here the slot will always have the same type of component, so the state is created only once.
                                    state.setComponent(4, "count_up");
                                    state.setComponent(13, "counter");
                                    if (count.get() > 0) {
                                        // These components may be cleared when count == 0, so the state is recreated whenever the count changes from 0 to >0.
                                        state.setComponent(22, "count_down");
                                        state.setComponent(10, "reset");
                                    }
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
                                                    count.update(0); // The update method changes the value of the signal and prompts the listener of the signal to re-render.
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


}
