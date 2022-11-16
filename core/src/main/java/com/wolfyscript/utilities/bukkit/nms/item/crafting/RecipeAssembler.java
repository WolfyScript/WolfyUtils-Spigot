package com.wolfyscript.utilities.bukkit.nms.item.crafting;

import java.util.Optional;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface RecipeAssembler<T extends Inventory> {

    Optional<ItemStack> assemble(T inventory);
}
