package com.wolfyscript.utilities.bukkit.gui.rendering

import com.wolfyscript.utilities.bukkit.adapters.PlayerImpl
import com.wolfyscript.utilities.bukkit.eval.context.EvalContextPlayer
import com.wolfyscript.utilities.eval.context.EvalContext
import com.wolfyscript.utilities.gui.Component
import com.wolfyscript.utilities.gui.GuiHolder
import com.wolfyscript.utilities.gui.ItemStackContext
import com.wolfyscript.utilities.gui.Position
import com.wolfyscript.utilities.gui.rendering.RenderContext
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

class InvGUIRenderContext(private val renderer: InventoryGUIRenderer) : RenderContext {
    private var currentNode: Component? = null
    private var slotOffsetToParent = 0
    private lateinit var currentPosition: Position

    fun offset(position: Position) {
        currentPosition = position
    }

    fun offset() : Position {
        return currentPosition
    }

    fun setSlotOffset(offset: Int) {
        this.slotOffsetToParent = offset
    }

    override fun currentOffset(): Int {
        return slotOffsetToParent
    }

    override fun enterNode(component: Component) {
        this.currentNode = component
        this.slotOffsetToParent = component.offset()
    }

    override fun exitNode() {
        this.currentNode = null
    }

    override fun getCurrentComponent(): Component? {
        return currentNode
    }

    fun createContext(guiHolder: GuiHolder, tagResolvers: TagResolver): ItemStackContext {
        return object : ItemStackContext {
            override fun resolvers(): TagResolver {
                return tagResolvers
            }

            override fun miniMessage(): MiniMessage {
                return renderer.runtime.wolfyUtils.chat.miniMessage
            }

            override fun evalContext(): EvalContext {
                return EvalContextPlayer((guiHolder.player as PlayerImpl).bukkitRef)
            }

            override fun holder(): GuiHolder {
                return guiHolder
            }
        }
    }
}
