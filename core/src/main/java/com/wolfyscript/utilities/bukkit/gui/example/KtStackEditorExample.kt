package com.wolfyscript.utilities.bukkit.gui.example

import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl
import com.wolfyscript.utilities.gui.*
import com.wolfyscript.utilities.gui.reactivity.Signal
import com.wolfyscript.utilities.gui.reactivity.createSignal
import com.wolfyscript.utilities.platform.adapters.ItemStack
import org.bukkit.inventory.meta.ItemMeta

private class StackEditorStore {
    private var stack: ItemStack? = null

    fun setStack(stack: ItemStack?) {
        this.stack = stack;
    }

    fun getStack(): ItemStack? {
        return this.stack
    }
}

private enum class Tab {
    NONE,
    DISPLAY_NAME,
    LORE,
}

fun register(manager: GuiAPIManager) {
    manager.registerGuiFromFiles("stack_editor") {
        window {
            /*
             This whole construction is only called upon the initiation and creates a reactivity graph
             from the signals and effects used and only updates the necessary parts at runtime.
             */

            size(9 * 6)

            // Persistent data stores
            val stackToEdit = createStore({ StackEditorStore() }, { getStack() }, { setStack(it) })
            // Weak data signals
            val selectedTab = createSignal(Tab.NONE)

            reactive {
                // Reactive parts are only called when a signal used inside this closure is updated.
                val itemStack = stackToEdit.get()
                if (itemStack == null || itemStack.item == null || itemStack.item.key == "air") {
                    return@reactive null
                }

                when (selectedTab.get()) {
                    Tab.DISPLAY_NAME -> displayNameTab(stackToEdit)

                    Tab.LORE ->
                        group("lore_tab") {
                            button("edit_lore") {
                                interact { _, _ -> InteractionResult.cancel(true) }
                            }
                            button("clear_lore") {
                                interact { _, _ -> InteractionResult.cancel(true) }
                            }
                        }

                    else -> null
                }
            }
            // The state of a component is only reconstructed if the slot it is positioned at changes.
            // Here the slot will always have the same type of component, so the state is created only once.
            slot("stack_slot") {
                interact { _, _ -> InteractionResult.cancel(false) }
                onValueChange { v -> stackToEdit.set(v) }
                value(stackToEdit)
            }
            button("display_name_tab_selector") {
                interact { _, _ ->
                    selectedTab.set(Tab.DISPLAY_NAME)
                    InteractionResult.cancel(true)
                }
            }
            button("lore_tab_selector") {
                interact { _, _ ->
                    selectedTab.set(Tab.LORE)
                    InteractionResult.cancel(true)
                }
            }
        }
    }
}

fun ReactiveRenderBuilder.displayNameTab(stackToEdit: Signal<ItemStack?>): ReactiveRenderBuilder.ReactiveResult {
    return group("display_name_tab") {
        button("set_display_name") {
            interact { runtime, _ ->
                runtime.setTextInputCallback { _, _, s, _ ->
                    stackToEdit.update { stack ->
                        if (stack is ItemStackImpl) {
                            val bukkitStack = stack.bukkitRef!!;
                            val meta: ItemMeta = bukkitStack.itemMeta;
                            meta.setDisplayName(s);
                            bukkitStack.setItemMeta(meta);
                            stackToEdit.set(stack);
                        }
                        stack
                    }
                    true
                }
                InteractionResult.cancel(true)
            }
        }
        button("reset_display_name") {
            interact { _, _ ->
                stackToEdit.update { stack ->
                    if (stack is ItemStackImpl) {
                        val bukkitStack = stack.bukkitRef!!;
                        val meta: ItemMeta = bukkitStack.itemMeta;
                        meta.setDisplayName(null);
                        bukkitStack.setItemMeta(meta);
                    }
                    stack
                }
                InteractionResult.cancel(true)
            }
        }
    }
}
