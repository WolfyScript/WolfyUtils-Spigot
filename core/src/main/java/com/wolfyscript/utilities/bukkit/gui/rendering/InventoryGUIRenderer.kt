package com.wolfyscript.utilities.bukkit.gui.rendering

import com.wolfyscript.utilities.bukkit.WolfyCoreImpl
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit
import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl
import com.wolfyscript.utilities.bukkit.adapters.PlayerImpl
import com.wolfyscript.utilities.bukkit.gui.BukkitInventoryGuiHolder
import com.wolfyscript.utilities.bukkit.nms.inventory.InventoryUpdate
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig
import com.wolfyscript.utilities.gui.*
import com.wolfyscript.utilities.gui.components.Button
import com.wolfyscript.utilities.gui.components.ComponentCluster
import com.wolfyscript.utilities.gui.components.StackInputSlot
import com.wolfyscript.utilities.gui.rendering.PropertyPosition
import com.wolfyscript.utilities.gui.rendering.Renderer
import com.wolfyscript.utilities.gui.rendering.RenderingGraph
import com.wolfyscript.utilities.gui.rendering.RenderingNode
import com.wolfyscript.utilities.platform.adapters.ItemStack
import com.wolfyscript.utilities.versioning.MinecraftVersion
import com.wolfyscript.utilities.versioning.ServerVersion
import com.wolfyscript.utilities.world.items.ItemStackConfig
import net.kyori.adventure.text.serializer.craftbukkit.BukkitComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import java.util.*

class InventoryGUIRenderer(val runtime: ViewRuntimeImpl) : Renderer<InvGUIRenderContext> {

    private var inventory: Inventory? = Bukkit.createInventory(null, 27)
    private val computedProperties: MutableMap<Long, ComputedProperties> = mutableMapOf()
    private var window: Window? = null

    fun changeWindow(window: Window) {
        // No active Window or it is another Window, need to recreate inventory
        val guiHolder: GuiHolder = GuiHolderImpl(window, runtime, null)
        val holder = BukkitInventoryGuiHolder(guiHolder)
        val title: net.kyori.adventure.text.Component = window.title()

        inventory = if ((window.wolfyUtils as WolfyUtilsBukkit).core.compatibilityManager.isPaper) {
            // Paper has direct Adventure support, so use it for better titles!
            getInventoryType(window).map { inventoryType: InventoryType? ->
                Bukkit.createInventory(holder, inventoryType!!, title)
            }.orElseGet {
                Bukkit.createInventory(holder, window.size.orElseThrow {
                    IllegalStateException("Invalid window type/size definition.")
                }, title)
            }
        } else {
            getInventoryType(window).map { inventoryType: InventoryType? ->
                Bukkit.createInventory(holder, inventoryType!!, BukkitComponentSerializer.legacy().serialize(title))
            }.orElseGet {
                Bukkit.createInventory(holder, window.size.orElseThrow {
                    IllegalStateException("Invalid window type/size definition.")
                }, BukkitComponentSerializer.legacy().serialize(title))
            }
        }
        holder.setActiveInventory(inventory)
    }

    override fun render() {
        if (inventory == null) return
        if (window == null) return

        val graph = runtime.renderingGraph

        val context = InvGUIRenderContext(this)
        computedProperties[0] = ComputedProperties(ComputedProperties.Positioning.FIXED, 0, window!!.width(), window!!.height())
        context.setSlotOffset(0)

        for (child in graph.children(0)) {
            graph.getNode(child)?.let {
                renderChild(graph, context, it)
            }
        }

    }

    private fun renderChild(graph: RenderingGraph, context: InvGUIRenderContext, node: RenderingNode) {
        when (val component = node.component) {
            is Button -> {
                InventoryButtonComponentRenderer().render(context, component)
            }

            is ComponentCluster -> {
                InventoryGroupComponentRenderer().render(context, component)
            }

            is StackInputSlot -> {

            }
        }
    }

    override fun renderComponent(component: Component, context: InvGUIRenderContext) {


    }

    override fun update() {

    }

    private fun getInventoryType(window: Window): Optional<InventoryType> {
        return window.type.map { type: WindowType? ->
            when (type) {
                WindowType.CUSTOM -> InventoryType.CHEST
                WindowType.HOPPER -> InventoryType.HOPPER
                WindowType.DROPPER -> InventoryType.DROPPER
                WindowType.DISPENSER -> InventoryType.DISPENSER
                null -> InventoryType.CHEST
            }
        }
    }

    fun updateTitle(player: com.wolfyscript.utilities.platform.adapters.Player, component: net.kyori.adventure.text.Component?) {
        val bukkitPlayer = (player as PlayerImpl).bukkitRef
        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0))) {
            bukkitPlayer.openInventory.title =
                net.kyori.adventure.platform.bukkit.BukkitComponentSerializer.legacy().serialize(
                    component!!
                )
        } else {
            InventoryUpdate.updateInventory(
                (runtime.wolfyUtils.core as WolfyCoreImpl).wolfyUtils.plugin,
                bukkitPlayer,
                component
            )
        }
    }

    fun setStack(i: Int, itemStackConfig: ItemStackConfig?) {
        if (itemStackConfig == null) {
            inventory!!.setItem(i, null)
            return
        }
        require(itemStackConfig is BukkitItemStackConfig) {
            String.format(
                "Cannot render stack config! Invalid stack config type! Expected '%s' but received '%s'.",
                BukkitItemStackConfig::class.java.name, itemStackConfig.javaClass.name
            )
        }

        inventory!!.setItem(i, itemStackConfig.constructItemStack().bukkitRef)
    }

    fun renderStack(position: PropertyPosition, itemStack: ItemStack?) {
        if (itemStack == null) {
            setNativeStack(position.slot(), null)
            return
        }
        require(itemStack is ItemStackImpl) {
            String.format(
                "Cannot render stack! Invalid stack config type! Expected '%s' but received '%s'.",
                ItemStackImpl::class.java.name, itemStack.javaClass.name
            )
        }

        setNativeStack(position.slot(), itemStack.bukkitRef)
    }

    fun renderStack(position: PropertyPosition, itemStackConfig: ItemStackConfig, itemStackContext: ItemStackContext) {
        require(itemStackConfig is BukkitItemStackConfig) {
            String.format(
                "Cannot render stack config! Invalid stack config type! Expected '%s' but received '%s'.",
                BukkitItemStackConfig::class.java.name, itemStackConfig.javaClass.name
            )
        }

        setNativeStack(
            position.slot(),
            itemStackConfig.constructItemStack(
                null,
                runtime.wolfyUtils.chat.miniMessage,
                itemStackContext.resolvers()
            ).bukkitRef
        )
    }

    private fun setNativeStack(i: Int, itemStack: org.bukkit.inventory.ItemStack?) {
        //checkIfSlotInBounds(i);
        if (itemStack == null) {
            inventory!!.setItem(i, null)
            return
        }
        inventory!!.setItem(i, itemStack)
    }


    class ComputedProperties(
        var positioning: Positioning,
        var slotPosition: Int,
        var width: Int,
        var height: Int
    ) {

        enum class Positioning {
            STATIC,
            RELATIVE,
            ABSOLUTE,
            FIXED
        }


    }

}