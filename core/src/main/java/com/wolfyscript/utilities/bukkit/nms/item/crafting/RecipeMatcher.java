package com.wolfyscript.utilities.bukkit.nms.item.crafting;

import org.bukkit.World;
import org.bukkit.inventory.Inventory;

@FunctionalInterface
public interface RecipeMatcher<T extends Inventory> {

    boolean match(T inventory, World world);
}
