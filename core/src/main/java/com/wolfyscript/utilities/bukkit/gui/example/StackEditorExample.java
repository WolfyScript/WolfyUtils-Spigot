package com.wolfyscript.utilities.bukkit.gui.example;

import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl;
import com.wolfyscript.utilities.bukkit.chat.BukkitChat;
import com.wolfyscript.utilities.bukkit.gui.GUIHolder;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.adapters.ItemStack;
import com.wolfyscript.utilities.common.chat.Chat;
import com.wolfyscript.utilities.common.gui.GuiAPIManager;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.InteractionResult;
import com.wolfyscript.utilities.common.gui.ReactiveRenderBuilder;
import com.wolfyscript.utilities.common.gui.components.ButtonBuilder;
import com.wolfyscript.utilities.common.gui.components.ComponentClusterBuilder;
import com.wolfyscript.utilities.common.gui.components.StackInputSlotBuilder;
import com.wolfyscript.utilities.common.gui.signal.Signal;
import com.wolfyscript.utilities.common.gui.signal.Store;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.WeakHashMap;

public class StackEditorExample {

    private static final Map<GuiViewManager, StackEditorStore> editorStores = new WeakHashMap<>();

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
        manager.registerGuiFromFiles("stack_editor", builder -> builder
                .window(mainMenu -> mainMenu
                        .size(9 * 6)
                        .construct((renderer) -> {
                            // This is only called upon creation of the state. So this is not called when the signal is updated!
                            StackEditorStore editorStore = editorStores.computeIfAbsent(renderer.viewManager(), guiViewManager -> new StackEditorStore());

                            // Persistent data stores
                            Store<ItemStack> stackToEdit = renderer.syncStore("stack_to_edit", ItemStack.class, editorStore::getStack, editorStore::setStack);

                            // Weak data signals
                            Signal<Tab> selectedTab = renderer.signal("selected_tab", Tab.class, () -> Tab.NONE);

                            renderer
                                    .reactive(reactiveBuilder -> {
                                        // Reactive parts are called everytime the signal used inside this closure is updated.
                                        ItemStack itemStack = stackToEdit.get();
                                        if (itemStack == null || itemStack.getItem() == null || itemStack.getItem().getKey().equals("air"))
                                            return null;

                                        return switch (selectedTab.get()) {
                                            case DISPLAY_NAME -> displayNameTab(reactiveBuilder, stackToEdit);
                                            case LORE ->
                                                    reactiveBuilder.render("lore_tab", ComponentClusterBuilder.class, displayNameClusterBuilder -> displayNameClusterBuilder
                                                            .render("edit_lore", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                                    .interact((holder, details) -> {

                                                                        return InteractionResult.cancel(true);
                                                                    }))
                                                            .render("clear_lore", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                                                    .interact((holder, details) -> {

                                                                        return InteractionResult.cancel(true);
                                                                    })));
                                            default -> null; // No tab selected!
                                        };
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
                                                selectedTab.set(Tab.DISPLAY_NAME);
                                                return InteractionResult.cancel(true);
                                            }))
                                    .render("lore_tab_selector", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                                            .interact((holder, details) -> {
                                                selectedTab.set(Tab.LORE);
                                                return InteractionResult.cancel(true);
                                            }));
                        })
                )
        );
    }

    static ReactiveRenderBuilder.ReactiveResult displayNameTab(ReactiveRenderBuilder reactiveBuilder, Signal<ItemStack> stackToEdit) {
        return reactiveBuilder.render("display_name_tab", ComponentClusterBuilder.class, displayNameClusterBuilder -> displayNameClusterBuilder
                .render("set_display_name", ButtonBuilder.class, buttonBuilder -> buttonBuilder
                        .interact((holder, details) -> {
                            BukkitChat chat = (BukkitChat) holder.getViewManager().getWolfyUtils().getChat();
                            Player player = ((GUIHolder) holder).getBukkitPlayer();
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
                .render("reset_display_name", ButtonBuilder.class, buttonBuilder -> buttonBuilder
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
