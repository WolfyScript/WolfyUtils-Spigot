package com.wolfyscript.utilities.bukkit.gui.example;

import com.wolfyscript.utilities.bukkit.WolfyCoreImpl;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.adapters.ItemStack;
import com.wolfyscript.utilities.common.gui.GuiAPIManager;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.Position;
import com.wolfyscript.utilities.common.gui.signal.Signal;
import com.wolfyscript.utilities.common.gui.components.ButtonBuilder;
import com.wolfyscript.utilities.common.gui.components.ComponentClusterBuilder;
import com.wolfyscript.utilities.common.gui.components.StackInputSlotBuilder;
import com.wolfyscript.utilities.common.gui.signal.Store;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderStringConst;

import java.util.HashMap;
import java.util.Map;

public class TestGUI {

    private WolfyCoreImpl core;

    public TestGUI(WolfyCoreImpl core) {
        this.core = core;
    }

    public void initCodeOnly() {
        GuiAPIManager manager = core.getWolfyUtils().getGUIManager();
        manager.registerGui("counter", builder -> builder
                .window(mainMenu -> mainMenu
                        .size(27)
                        // Creates the signal this component will track and children can listen to
                        .construct(rendering -> {
                            Signal<Integer> count = rendering.signal("count", Integer.class, () -> 0);

                            rendering
                                    // The state of a component is only reconstructed if the slot it is positioned at changes.

                                    // Here the slot will always have the same type of component, so the state is created only once.
                                    // Static Rendering, uses the positions specified previously!
                                    .renderAt(Position.relative(4), "count_up", ButtonBuilder.class, settingsBtn -> settingsBtn
                                            .icon(icon -> icon
                                                    .stack(() -> {
                                                        BukkitItemStackConfig config = new BukkitItemStackConfig(core.getWolfyUtils(), "minecraft:green_concrete");
                                                        config.setName(new ValueProviderStringConst(core.getWolfyUtils(), "<green><b>Count Up"));
                                                        return config;
                                                    })
                                            )
                                            .interact((guiHolder, interactionDetails) -> {
                                                count.update(integer -> ++integer);
                                                return InteractionResult.cancel(true);
                                            }))
                                    .renderAt(Position.relative(13), "counter", ButtonBuilder.class, settingsBtn -> settingsBtn
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
                                            return reactiveBuilder
                                                    .render("count_down_reset", ComponentClusterBuilder.class, b -> b
                                                            .renderAt(Position.absolute(22), "count_down", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                                    .icon(icon -> icon
                                                                            .stack(() -> {
                                                                                BukkitItemStackConfig config = new BukkitItemStackConfig(core.getWolfyUtils(), "minecraft:red_concrete");
                                                                                config.setName(new ValueProviderStringConst(core.getWolfyUtils(), "<red><b>Count Down"));
                                                                                return config;
                                                                            })
                                                                    )
                                                                    .interact((guiHolder, interactionDetails) -> {
                                                                        count.update(integer -> --integer);
                                                                        return InteractionResult.cancel(true);
                                                                    }))
                                                            .renderAt(Position.absolute(10), "reset", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                                    .icon(icon -> icon.stack(() -> {
                                                                        BukkitItemStackConfig config = new BukkitItemStackConfig(core.getWolfyUtils(), "minecraft:tnt");
                                                                        config.setName(new ValueProviderStringConst(core.getWolfyUtils(), "<b><red>Reset Clicks!"));
                                                                        return config;
                                                                    }))
                                                                    .interact((guiHolder, interactionDetails) -> {
                                                                        count.set(0); // The set method changes the value of the signal and prompts the listener of the signal to re-render.
                                                                        return InteractionResult.cancel(true);
                                                                    }))
                                                    );
                                        }
                                        return null;
                                    });
                        })
                )
        );
    }

    public void initWithConfig() {
        GuiAPIManager manager = core.getWolfyUtils().getGUIManager();
        CounterExample.register(manager);
        StackEditorExample.register(manager);
    }




}
