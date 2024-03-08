package com.wolfyscript.utilities.bukkit.data

import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.bukkit.WolfyCoreImpl
import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl
import com.wolfyscript.utilities.bukkit.world.items.data.BannerPatternsImpl
import com.wolfyscript.utilities.bukkit.world.items.data.MapInfoImpl
import com.wolfyscript.utilities.bukkit.world.items.data.UnbreakableImpl
import com.wolfyscript.utilities.bukkit.world.items.toWrapper
import com.wolfyscript.utilities.data.DataKey
import com.wolfyscript.utilities.data.Keys
import com.wolfyscript.utilities.platform.adapters.ItemStack
import com.wolfyscript.utilities.world.items.data.*
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.DyeColor
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.*

class KeysImpl : Keys {

    companion object {
        val CUSTOM_NAME: DataKey<Component> = DataKeyImpl({ displayName() }, { data -> displayName(data) })

    }

    override fun attributeModifiers(): DataKey<AttributeModifiers> {
        TODO("Not yet implemented")
    }

    override fun bannerPatterns(): DataKey<BannerPatterns> = DataKeyImpl(
        { if (this is BannerMeta) BannerPatternsImpl(patterns) else BannerPatternsImpl(emptyList()) },
        { bannerPatterns ->
            if (this is BannerMeta && bannerPatterns is BannerPatternsImpl) {
                patterns = bannerPatterns.layers().map { it.toBukkit() }
            }
        })

    override fun baseColor(): DataKey<DyedColor> = DataKeyImpl({
        if (this is BannerMeta) {
            // TODO: Where is the non-deprecated API?
            if(baseColor != null) {
                // TODO: return@DataKeyImpl baseColor.toWrapper()
            }
        }
        null
    }, {
        if (this is BannerMeta) {
            baseColor = DyeColor.valueOf(toString())
        }
    })

    override fun bees(): DataKey<Bees> {
        TODO("Not yet implemented")
    }

    override fun blockEntityData(): DataKey<BlockEntityData> {
        TODO("Not yet implemented")
    }

    override fun blockState(): DataKey<BlockState> {
        TODO("Not yet implemented")
    }

    override fun bucketEntityData(): DataKey<BucketEntityData> {
        TODO("Not yet implemented")
    }

    override fun bundleContents(): DataKey<List<ItemStack>> = DataKeyImpl({
        if (this is BundleMeta) {
            return@DataKeyImpl items.map { ItemStackImpl(WolfyCoreImpl.getInstance().wolfyUtils, it) }
        }
        return@DataKeyImpl emptyList()
    }, {

    })

    override fun canBreak(): DataKey<CanBreak> {
        TODO("Not yet implemented")
    }

    override fun canPlaceOn(): DataKey<CanPlaceOn> {
        TODO("Not yet implemented")
    }

    override fun chargedProjectiles(): DataKey<ChargedProjectiles> {
        TODO("Not yet implemented")
    }

    override fun container(): DataKey<Container> {
        TODO("Not yet implemented")
    }

    override fun containerLoot(): DataKey<ContainerLoot> {
        TODO("Not yet implemented")
    }

    override fun customData() {
        TODO("Not yet implemented")
    }

    override fun customModelData(): DataKey<Int> = DataKeyImpl({
        if (hasCustomModelData()) {
            customModelData
        }
        null
    }, {
        setCustomModelData(it)
    })

    override fun customName(): DataKey<Component> = DataKeyImpl({ displayName() }, { data -> displayName(data) })

    override fun damage(): DataKey<Int> = DataKeyImpl({
        if (this is Damageable) {
            damage
        }
        null
    }, {
        if (this is Damageable) {
            damage = it
        }
    })

    override fun debugStickState(): DataKey<DebugStickState> {
        TODO("Not yet implemented")
    }

    override fun dyedColor(): DataKey<DyedColor> = DataKeyImpl({
        if (this is ColorableArmorMeta) {
            color // TODO
        }
        null
    }, {

    })

    override fun enchantmentGlintOverride(): DataKey<Boolean> {
        TODO("Not yet implemented")
    }

    override fun enchantments(): DataKey<Enchantments> {
        TODO("Not yet implemented")
    }

    override fun entityData(): DataKey<EntityData> {
        TODO("Not yet implemented")
    }

    override fun fireworkExplosion(): DataKey<FireworkExplosion> {
        TODO("Not yet implemented")
    }

    override fun fireworks(): DataKey<Fireworks> {
        TODO("Not yet implemented")
    }

    override fun hideAdditionalTooltip(): DataKey<HideAdditionalTooltip> {
        TODO("Not yet implemented")
    }

    override fun instrument(): DataKey<NamespacedKey> {
        TODO("Not yet implemented")
    }

    override fun intangibleProjectile(): DataKey<IntangibleProjectiles> {
        TODO("Not yet implemented")
    }

    override fun itemLore(): DataKey<ItemLore> {
        TODO("Not yet implemented")
    }

    override fun lock(): DataKey<String> = DataKeyImpl({
        TODO("Not yet implemented")
    }, {
        TODO("Not yet implemented")
    })

    override fun lodestoneTracker(): DataKey<LodestoneTracker> {
        TODO("Not yet implemented")
    }

    override fun mapColor(): DataKey<Int> {
        TODO("Not yet implemented")
    }

    override fun mapDecorations(): DataKey<MapDecorations> {
        TODO("Not yet implemented")
    }

    override fun mapId(): DataKey<Int> = DataKeyImpl({
        if (this is MapMeta && hasMapId()) {
            mapId
        }
        null
    }, {
        if (this is MapMeta) {
            val map = Bukkit.getMap(it)
            if (map != null) {
                mapView = map
            }
        }
    })

    override fun mapInfo(): DataKey<MapInfo> = DataKeyImpl({
        if (this is MapMeta) {

        }
        MapInfoImpl()
    }, {
        if (this is MapMeta) {

        }
    })

    override fun noteBlockSound(): DataKey<NamespacedKey> {
        TODO("Not yet implemented")
    }

    override fun potDecorations(): DataKey<List<NamespacedKey>> {
        TODO("Not yet implemented")
    }

    override fun potionContents(): DataKey<PotionContents> {
        TODO("Not yet implemented")
    }

    override fun profile(): DataKey<Profile> {
        TODO("Not yet implemented")
    }

    override fun recipes(): DataKey<List<NamespacedKey>> {
        TODO("Not yet implemented")
    }

    override fun repairCost(): DataKey<Int> = DataKeyImpl({
        if (this is Repairable) {
            this.repairCost
        }
        null
    }, {
        if (this is Repairable) {
            repairCost = it
        }
    })

    override fun storedEnchantments(): DataKey<Enchantments> {
        TODO("Not yet implemented")
    }

    override fun suspiciousStew(): DataKey<SuspiciousStew> {
        TODO("Not yet implemented")
    }

    override fun trim(): DataKey<Trim> {
        TODO("Not yet implemented")
    }

    override fun unbreakable(): DataKey<Unbreakable> = DataKeyImpl({
        if (isUnbreakable) {
            val showInTooltip = hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)
            UnbreakableImpl(showInTooltip)
        }
        null
    }, { data ->
        isUnbreakable = true
        if (data.showInTooltip()) {
            addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        } else {
            removeItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        }
    })

    override fun writableBookContents(): DataKey<WrittenBookContents> {
        TODO("Not yet implemented")
    }

    override fun writtenBookContents(): DataKey<WrittenBookContents> {
        TODO("Not yet implemented")
    }


}