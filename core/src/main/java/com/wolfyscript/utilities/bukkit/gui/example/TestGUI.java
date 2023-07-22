package com.wolfyscript.utilities.bukkit.gui.example;

import com.wolfyscript.utilities.bukkit.WolfyCoreImpl;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.adapters.ItemStack;
import com.wolfyscript.utilities.common.gui.GuiAPIManager;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.Signal;
import com.wolfyscript.utilities.common.gui.components.ButtonBuilder;
import com.wolfyscript.utilities.common.gui.components.ComponentClusterBuilder;
import com.wolfyscript.utilities.common.gui.components.StackInputSlotBuilder;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderStringConst;

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
                            Signal<Integer> count = rendering.createSignal("count", Integer.class, () -> 0);

                            rendering
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
                                            .interact((guiHolder, interactionDetails) -> {
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
                                                            .interact((guiHolder, interactionDetails) -> {
                                                                count.update(integer -> --integer);
                                                                return InteractionResult.cancel(true);
                                                            }))
                                                    .renderAt(10, "reset", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                            .icon(icon -> icon.stack(() -> {
                                                                BukkitItemStackConfig config = new BukkitItemStackConfig(core.getWolfyUtils(), "minecraft:tnt");
                                                                config.setName(new ValueProviderStringConst(core.getWolfyUtils(), "<b><red>Reset Clicks!"));
                                                                return config;
                                                            }))
                                                            .interact((guiHolder, interactionDetails) -> {
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
        registerCounterExample(manager);
        registerStackEditorExample(manager);
    }

    private void registerCounterExample(GuiAPIManager manager) {
        manager.registerGuiFromFiles("example_counter", builder -> builder
                .window(mainMenu -> mainMenu
                        .size(9 * 3)
                        .construct((renderer) -> {
                            // This is only called upon creation of the component. So this is not called when the signal is updated!
                            Signal<Integer> count = renderer.createSignal("count", Integer.class, () -> 0);

                            renderer
                                    .titleSignals(count)
                                    // Sometimes we want to render components dependent on signals
                                    .reactive(reactiveBuilder -> {
                                        if (count.get() > 0) {
                                            reactiveBuilder
                                                    .render("count_down", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                            .interact((guiHolder, interactionDetails) -> {
                                                                count.update(old -> --old);
                                                                return InteractionResult.cancel(true);
                                                            }))
                                                    .render("reset", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                            .interact((guiHolder, interactionDetails) -> {
                                                                count.set(0); // The set method changes the value of the signal and prompts the listener of the signal to re-render.
                                                                return InteractionResult.cancel(true);
                                                            }));
                                        }
                                    })
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

    private void registerStackEditorExample(GuiAPIManager manager) {
        manager.registerGuiFromFiles("stack_editor", builder -> builder
                .window(mainMenu -> mainMenu
                        .size(9 * 6)
                        .construct((renderer) -> {
                            // This is only called upon creation of the state. So this is not called when the signal is updated!
                            Signal<ItemStack> stackToEdit = renderer.createSignal("stack_to_edit", ItemStack.class, () -> null);
                            Signal<String> selectedTab = renderer.createSignal("selected_tab", String.class, () -> "");

                            renderer
                                    .reactive(reactiveBuilder -> {
                                        // Reactive parts are called everytime the signal used inside this closure is updated.
                                        ItemStack stack = stackToEdit.get();
                                        if (stack == null || stack.getItem() == null || stack.getItem().getKey().equals("air"))
                                            return;

                                        switch (selectedTab.get()) {
                                            case "display_name" ->
                                                    reactiveBuilder.render("display_name_tab", ComponentClusterBuilder.class, displayNameClusterBuilder -> displayNameClusterBuilder
                                                            .render("set_display_name", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                                    .interact((holder, details) -> {

                                                                        return InteractionResult.cancel(true);
                                                                    }))
                                                            .render("reset_display_name", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                                    .interact((holder, details) -> {

                                                                        return InteractionResult.cancel(true);
                                                                    }))
                                                    );
                                            case "lore" ->
                                                    reactiveBuilder.render("lore_tab", ComponentClusterBuilder.class, displayNameClusterBuilder -> displayNameClusterBuilder
                                                            .render("edit_lore", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                                    .interact((holder, details) -> {

                                                                        return InteractionResult.cancel(true);
                                                                    }))
                                                            .render("clear_lore", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                                    .interact((holder, details) -> {

                                                                        return InteractionResult.cancel(true);
                                                                    })));
                                            default -> {
                                                // No tab selected!
                                            }
                                        }
                                    })
                                    // The state of a component is only reconstructed if the slot it is positioned at changes.
                                    // Here the slot will always have the same type of component, so the state is created only once.
                                    .render("stack_slot", StackInputSlotBuilder.class, inputSlotBuilder -> inputSlotBuilder
                                            .interact((guiHolder, interactionDetails) -> InteractionResult.cancel(false))
                                            .onValueChange(stackToEdit::set)
                                            .value(stackToEdit)
                                    )
                                    .render("display_name_tab_selector", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                            .interact((holder, details) -> {
                                                selectedTab.set("display_name");
                                                return InteractionResult.cancel(true);
                                            }))
                                    .render("lore_tab_selector", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                            .interact((holder, details) -> {
                                                selectedTab.set("lore");
                                                return InteractionResult.cancel(true);
                                            }));
                        })
                )
        );
    }


}
