package com.wolfyscript.utilities.bukkit.gui;

import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.gui.GuiAPIManager;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.StateHook;
import com.wolfyscript.utilities.common.gui.WindowType;
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

        manager.registerRouter("main", builder -> builder
                .children(children -> children
                        .window("mainmenu", mainMenu -> mainMenu
                                .title((guiHolder, window) -> Component.text("Main Menu"))
                                .size(27)
                                .useSignal(COUNT, Integer.class, count -> count.defaultValue(state -> 0))
                                .render((guiHolder, state) -> {
                                    // Place child components into the GUI
                                    Signal.Value<Integer> count = state.captureSignal(COUNT, Integer.class);
                                    state.setComponent(13, "counter"); // references the registered child component with the id "settings"
                                    if (count.get() > 0) {
                                        state.setComponent(20, "reset");
                                    }
                                })
                                .children(mainMenuChildren -> mainMenuChildren
                                        .button("counter", settingsBtn -> settingsBtn
                                                .icon(icon -> icon
                                                        .stack(() -> {
                                                            BukkitItemStackConfig config = new BukkitItemStackConfig(core.getWolfyUtils(), "minecraft:redstone");
                                                            config.setName(new ValueProviderStringConst(core.getWolfyUtils(), "Clicked <signal:count:1> times!"));
                                                            return config;
                                                        })
                                                        .dynamic()
                                                )
                                                .interact((guiHolder, componentState, interactionDetails) -> {
                                                    Signal.Value<Integer> count = componentState.getParent().captureSignal(COUNT, Integer.class);
                                                    count.update(integer -> ++integer);
                                                    return InteractionResult.cancel(true);
                                                })
                                        )
                                        .button("reset", resetBtn -> resetBtn
                                                .icon(icon -> icon.stack(() -> {
                                                    BukkitItemStackConfig config = new BukkitItemStackConfig(core.getWolfyUtils(), "minecraft:red_concrete");
                                                    config.setName(new ValueProviderStringConst(core.getWolfyUtils(), "Reset Clicks!"));
                                                    return config;
                                                }))
                                                .interact((guiHolder, componentState, interactionDetails) -> {
                                                    Signal.Value<Integer> count = componentState.getParent().captureSignal(COUNT, Integer.class);
                                                    count.update(0);
                                                    return InteractionResult.cancel(true);
                                                })
                                        )
                                )
                        )
                )
        );
    }


}
