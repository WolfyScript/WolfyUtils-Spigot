package com.wolfyscript.utilities.bukkit.nms.item.crafting;

import org.bukkit.World;
import org.bukkit.inventory.Inventory;

@FunctionalInterface
public interface RecipeMatcher {

    boolean match(Inventory inventory, World world);
}
