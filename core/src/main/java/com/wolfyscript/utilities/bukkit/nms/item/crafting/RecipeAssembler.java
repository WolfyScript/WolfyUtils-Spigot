package com.wolfyscript.utilities.bukkit.nms.item.crafting;

import java.util.Optional;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface RecipeAssembler {

    Optional<ItemStack> assemble(Inventory inventory);
}
