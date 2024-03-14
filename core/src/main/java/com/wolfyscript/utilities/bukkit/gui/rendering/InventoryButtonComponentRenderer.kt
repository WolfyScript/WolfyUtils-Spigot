package com.wolfyscript.utilities.bukkit.gui.rendering

import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey
import com.wolfyscript.utilities.gui.components.Button
import com.wolfyscript.utilities.gui.rendering.ComponentRenderer
import com.wolfyscript.utilities.gui.rendering.PropertyPosition

class InventoryButtonComponentRenderer : ComponentRenderer<Button, InvGUIRenderContext> {

    override fun key(): NamespacedKey = BukkitNamespacedKey("wolfyutils", "inventory/button")

    override fun render(context: InvGUIRenderContext, component: Button) {



    }
}