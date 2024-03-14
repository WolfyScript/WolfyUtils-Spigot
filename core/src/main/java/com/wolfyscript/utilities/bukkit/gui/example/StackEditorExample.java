package com.wolfyscript.utilities.bukkit.gui.example;

import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl;
import com.wolfyscript.utilities.bukkit.chat.BukkitChat;
import com.wolfyscript.utilities.bukkit.gui.BukkitInventoryGuiHolder;
import com.wolfyscript.utilities.gui.GuiAPIManager;
import com.wolfyscript.utilities.gui.InteractionResult;
import com.wolfyscript.utilities.gui.ReactiveRenderBuilder;
import com.wolfyscript.utilities.gui.components.ButtonBuilder;
import com.wolfyscript.utilities.gui.components.ComponentClusterBuilder;
import com.wolfyscript.utilities.gui.components.StackInputSlotBuilder;
import com.wolfyscript.utilities.gui.reactivity.Signal;
import com.wolfyscript.utilities.platform.adapters.ItemStack;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class StackEditorExample {

    private static class StackEditorStore {

        private ItemStack stack;

        public ItemStack getStack() {
            return stack;
        }

        public void setStack(ItemStack stack) {
            this.stack = stack;
        }
    }

    private enum Tab {
        NONE,
        DISPLAY_NAME,
        LORE,
    }

    static void register(GuiAPIManager manager) {
        manager.registerGuiFromFiles("stack_editor", (builder) -> {
                    builder.window((mainMenu) -> {
                        mainMenu.size(9 * 6);
                        // This is only called upon creation of the state. So this is not called when the signal is updated!

                        // Persistent data stores
                        Signal<ItemStack> stackToEdit = mainMenu.createStore(viewRuntime -> new StackEditorStore(), StackEditorStore::getStack, StackEditorStore::setStack);

                        // Weak data signals
                        Signal<Tab> selectedTab = mainMenu.createSignal(Tab.class, r -> Tab.NONE);

                        mainMenu.reactive(reactiveBuilder -> {
                                    // Reactive parts are called everytime the signal used inside this closure is updated.
                                    ItemStack itemStack = stackToEdit.get();
                                    if (itemStack == null || itemStack.getItem() == null || itemStack.getItem().getKey().equals("air"))
                                        return null;

                                    return switch (selectedTab.get()) {
                                        case DISPLAY_NAME -> displayNameTab(reactiveBuilder, stackToEdit);
                                        case LORE ->
                                                reactiveBuilder.component("lore_tab", ComponentClusterBuilder.class, displayNameClusterBuilder -> displayNameClusterBuilder
                                                        .component("edit_lore", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                                .interact((holder, details) -> {

                                                                    return InteractionResult.cancel(true);
                                                                }))
                                                        .component("clear_lore", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                                .interact((holder, details) -> {

                                                                    return InteractionResult.cancel(true);
                                                                })));
                                        default -> null; // No tab selected!
                                    };
                                })
                                // The state of a component is only reconstructed if the slot it is positioned at changes.
                                // Here the slot will always have the same type of component, so the state is created only once.
                                .component("stack_slot", StackInputSlotBuilder.class, inputSlotBuilder -> inputSlotBuilder
                                        .interact((guiHolder, interactionDetails) -> InteractionResult.cancel(false))
                                        .onValueChange(stackToEdit::set)
                                        .value(stackToEdit)
                                )
                                .component("display_name_tab_selector", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                        .interact((holder, details) -> {
                                            selectedTab.set(Tab.DISPLAY_NAME);
                                            return InteractionResult.cancel(true);
                                        }))
                                .component("lore_tab_selector", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                        .interact((holder, details) -> {
                                            selectedTab.set(Tab.LORE);
                                            return InteractionResult.cancel(true);
                                        }));
                    });
                }
        );
    }

    static ReactiveRenderBuilder.ReactiveResult displayNameTab(ReactiveRenderBuilder reactiveBuilder, Signal<ItemStack> stackToEdit) {
        return reactiveBuilder.component("display_name_tab", ComponentClusterBuilder.class, displayNameClusterBuilder -> displayNameClusterBuilder
                .component("set_display_name", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                        .interact((holder, details) -> {
                            BukkitChat chat = (BukkitChat) holder.getViewManager().getWolfyUtils().getChat();
                            Player player = null;
                            chat.sendMessage(player, Component.text("Click me"));
                            holder.getViewManager().setTextInputCallback((p, guiViewManager, s, strings) -> {
                                stackToEdit.update(stack -> {
                                    if (stack instanceof ItemStackImpl stackImpl) {
                                        var bukkitStack = stackImpl.getBukkitRef();
                                        ItemMeta meta = bukkitStack.getItemMeta();
                                        meta.setDisplayName(s);
                                        bukkitStack.setItemMeta(meta);
                                        stackToEdit.set(stack);
                                    }
                                    return stack;
                                });
                                return true;
                            });
                            return InteractionResult.cancel(true);
                        }))
                .component("reset_display_name", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                        .interact((holder, details) -> {
                            stackToEdit.update(stack -> {
                                if (stack instanceof ItemStackImpl stackImpl) {
                                    var bukkitStack = stackImpl.getBukkitRef();
                                    ItemMeta meta = bukkitStack.getItemMeta();
                                    meta.setDisplayName(null);
                                    bukkitStack.setItemMeta(meta);
                                }
                                return stack;
                            });
                            return InteractionResult.cancel(true);
                        }))
        );
    }

}