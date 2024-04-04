package com.wolfyscript.utilities.bukkit.world.items.data

import com.wolfyscript.utilities.WolfyCore
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit
import com.wolfyscript.utilities.bukkit.WolfyCoreImpl
import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl
import com.wolfyscript.utilities.platform.adapters.ItemStack
import com.wolfyscript.utilities.world.items.data.ChargedProjectiles
import org.bukkit.inventory.meta.CrossbowMeta

class ChargedProjectilesImpl(val projectiles: List<ItemStack>) : ChargedProjectiles {

    companion object {
        internal val ITEM_META_CONVERTER = ItemMetaDataKeyConverter<ChargedProjectiles>(
            {
                if (this is CrossbowMeta) {
                    val projectiles = chargedProjectiles.map {
                        ItemStackImpl(WolfyCoreBukkit.getInstance().wolfyUtils, it)
                    }
                    return@ItemMetaDataKeyConverter ChargedProjectilesImpl(projectiles)
                }
                null
            },
            { TODO("Not yet implemented") }
        )

    }
}