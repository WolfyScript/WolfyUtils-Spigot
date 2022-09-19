package me.wolfyscript.utilities.api.nms.item.crafting;

import org.bukkit.World;
import org.bukkit.inventory.Inventory;

@FunctionalInterface
public interface RecipeMatcher {

    boolean match(Inventory inventory, World world);
}
