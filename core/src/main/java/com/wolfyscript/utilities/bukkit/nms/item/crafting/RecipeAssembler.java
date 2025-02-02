package com.wolfyscript.utilities.bukkit.nms.item.crafting;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@FunctionalInterface
public interface RecipeAssembler<T extends Inventory> {

    Optional<ItemStack> assemble(T inventory);
}
