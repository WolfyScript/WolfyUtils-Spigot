package com.wolfyscript.utilities.bukkit.gui.rendering

import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey
import com.wolfyscript.utilities.gui.components.ComponentCluster
import com.wolfyscript.utilities.gui.rendering.ComponentRenderer
import com.wolfyscript.utilities.gui.rendering.PropertyPosition

class InventoryGroupComponentRenderer : ComponentRenderer<ComponentCluster, InvGUIRenderContext> {

    override fun key(): NamespacedKey = BukkitNamespacedKey("wolfyutils", "inventory/group")

    override fun render(context: InvGUIRenderContext, component: ComponentCluster) { }
}