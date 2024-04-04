package com.wolfyscript.utilities.bukkit.data

import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl
import com.wolfyscript.utilities.data.DataComponentMap
import com.wolfyscript.utilities.data.DataKey
import com.wolfyscript.utilities.data.Keys
import com.wolfyscript.utilities.gui.functions.ReceiverFunction
import com.wolfyscript.utilities.platform.adapters.ItemStack

class ItemStackDataComponentMap internal constructor(private val itemStack: ItemStackImpl) : DataComponentMap<ItemStack> {

    override fun keySet(): Set<DataKey<*, ItemStack>> {
        TODO("Not yet implemented")
    }

    override fun remove(key: DataKey<*, ItemStack>): Boolean {
        TODO("Not yet implemented")
    }

    override fun size(): Int {
        return 0
    }

    override fun <T: Any> get(key: ReceiverFunction<Keys, DataKey<T, ItemStack>>): T? {
        TODO("Not yet implemented")
    }

    override fun has(key: DataKey<*, ItemStack>): Boolean {
        TODO("Not yet implemented")
    }

    override fun <T: Any> set(key: DataKey<T, ItemStack>, data: T) {

    }

}
