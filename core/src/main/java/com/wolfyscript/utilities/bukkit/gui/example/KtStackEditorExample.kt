package com.wolfyscript.utilities.bukkit.gui.example

import com.wolfyscript.utilities.data.ItemStackDataKeys
import com.wolfyscript.utilities.gui.GuiAPIManager
import com.wolfyscript.utilities.gui.InteractionResult
import com.wolfyscript.utilities.gui.ReactiveRenderBuilder
import com.wolfyscript.utilities.gui.reactivity.Signal
import com.wolfyscript.utilities.gui.reactivity.createSignal
import com.wolfyscript.utilities.platform.adapters.ItemStack

class StackEditorStore {
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
            val stackToEdit = createSignal { StackEditorStore() }
            // Weak data signals
            val selectedTab = createSignal(Tab.NONE)

            reactive {
                // Reactive parts are only called when a signal used inside this closure is updated.
                val itemStack = stackToEdit.get()?.getStack()
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
            slot("stack_slot") {
                interact { _, _ -> InteractionResult.cancel(false) }
                onValueChange { v ->
                    stackToEdit.update {
                        it.setStack(v)
                        it
                    }
                }
                value { stackToEdit.get()?.getStack() }
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

fun ReactiveRenderBuilder.displayNameTab(stackToEdit: Signal<StackEditorStore>): ReactiveRenderBuilder.ReactiveResult {
    return group("display_name_tab") {
        button("set_display_name") {
            interact { runtime, _ ->
                runtime.setTextInputCallback { _, _, s, _ ->
                    stackToEdit.update { store ->
                        store?.getStack()?.data()?.set(ItemStackDataKeys.CUSTOM_NAME, runtime.wolfyUtils.chat.miniMessage.deserialize(s))
                        store
                    }
                    true
                }
                InteractionResult.cancel(true)
            }
        }
        button("reset_display_name") {
            interact { _, _ ->
                stackToEdit.update { store ->
                    store?.getStack()?.data()?.remove(ItemStackDataKeys.CUSTOM_NAME)
                    store
                }
                InteractionResult.cancel(true)
            }
        }
    }
}
