package com.wolfyscript.utilities.bukkit.nms.item.crafting;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface RecipeRemainingItemsFunction<T extends Inventory> {

    Optional<List<ItemStack>> apply(T inventory);

}
