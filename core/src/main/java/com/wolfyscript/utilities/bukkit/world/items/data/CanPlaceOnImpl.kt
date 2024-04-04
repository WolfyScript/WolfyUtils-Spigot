package com.wolfyscript.utilities.bukkit.world.items.data

import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey
import com.wolfyscript.utilities.bukkit.WolfyCoreImpl
import com.wolfyscript.utilities.world.items.data.CanBreak
import com.wolfyscript.utilities.world.items.data.CanPlaceOn
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag

class CanPlaceOnImpl(override val showInTooltip: Boolean, private val blocks: List<NamespacedKey>) : CanPlaceOn {

    companion object {
        internal val ITEM_META_CONVERTER = ItemMetaDataKeyConverter<CanPlaceOn>({
            val show = !hasItemFlag(ItemFlag.HIDE_PLACED_ON)
            return@ItemMetaDataKeyConverter if (WolfyCoreImpl.getInstance().compatibilityManager.isPaper) {
                CanPlaceOnImpl(show, placeableKeys.map { BukkitNamespacedKey(it.namespace, it.key) })
            } else {
                CanPlaceOnImpl(show, canPlaceOn.map { BukkitNamespacedKey.fromBukkit(it.key) })
            }
        }, { placeOn ->
            if (WolfyCoreImpl.getInstance().compatibilityManager.isPaper) {
                setPlaceableKeys(placeOn.blocks().map { (it as BukkitNamespacedKey).bukkit() })
            } else {
                /*
                * WARNING: Possible LOSS of Information!
                *
                * Keys that are not valid materials are lost!
                * */
                canPlaceOn = placeOn.blocks().map { Material.getMaterial(it.toString()) }.toSet()
            }
        })
    }

    override fun blocks(): List<NamespacedKey> = blocks
}