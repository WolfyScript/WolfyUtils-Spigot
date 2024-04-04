package com.wolfyscript.utilities.bukkit.world.items.data

import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey
import com.wolfyscript.utilities.bukkit.WolfyCoreImpl
import com.wolfyscript.utilities.world.items.data.CanBreak
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag

class CanBreakImpl(override val showInTooltip: Boolean, private val blocks: List<NamespacedKey>) : CanBreak {

    companion object {
        internal val ITEM_META_CONVERTER = ItemMetaDataKeyConverter<CanBreak>({
            val show = !hasItemFlag(ItemFlag.HIDE_PLACED_ON)
            return@ItemMetaDataKeyConverter if (WolfyCoreImpl.getInstance().compatibilityManager.isPaper) {
                CanBreakImpl(show, destroyableKeys.map { BukkitNamespacedKey(it.namespace, it.key) })
            } else {
                CanBreakImpl(show, canDestroy.map { BukkitNamespacedKey.fromBukkit(it.key) })
            }
        }, { canBreak ->
            if (WolfyCoreImpl.getInstance().compatibilityManager.isPaper) {
                setDestroyableKeys(canBreak.blocks().map { (it as BukkitNamespacedKey).bukkit() })
            } else {
                /*
                * WARNING: Possible LOSS of Information!
                *
                * Keys that are not valid materials are lost!
                * */
                canDestroy = canBreak.blocks().map { Material.getMaterial(it.toString()) }.toSet()
            }
        })
    }

    override fun blocks(): List<NamespacedKey> = blocks
}