package com.wolfyscript.utilities.bukkit.data

import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl
import com.wolfyscript.utilities.data.DataComponentMap
import com.wolfyscript.utilities.data.DataKey
import com.wolfyscript.utilities.data.Keys
import com.wolfyscript.utilities.gui.functions.ReceiverFunction

class DataComponentMapImpl internal constructor(private val itemStack: ItemStackImpl) : DataComponentMap {

    override fun keySet(): Set<DataKey<*>> {
        TODO("Not yet implemented")
    }

    override fun remove(key: ReceiverFunction<Keys, DataKey<*>>): Boolean {
        TODO("Not yet implemented")
    }

    override fun size(): Int {
        return 0
    }

    override fun <T> get(key: ReceiverFunction<Keys, DataKey<T>>): T? {
        TODO("Not yet implemented")
    }

    override fun has(key: ReceiverFunction<Keys, DataKey<*>>): Boolean {
        TODO("Not yet implemented")
    }

    override fun <T> set(key: ReceiverFunction<Keys, DataKey<T>>, t: T) {

    }

}
