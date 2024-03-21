package com.wolfyscript.utilities.bukkit.gui.interaction

import com.wolfyscript.utilities.gui.InteractionResult
import com.wolfyscript.utilities.gui.ViewRuntime
import com.wolfyscript.utilities.gui.components.ComponentCluster
import com.wolfyscript.utilities.gui.interaction.ComponentInteractionHandler
import com.wolfyscript.utilities.gui.interaction.InteractionDetails

class InventoryGroupInteractionHandler : ComponentInteractionHandler<ComponentCluster> {

    override fun interact(
        runtime: ViewRuntime,
        component: ComponentCluster,
        details: InteractionDetails
    ): InteractionResult {
        return InteractionResult.def()
    }
}