package com.wolfyscript.utilities.bukkit.nms.item.crafting;

import me.wolfyscript.utilities.util.Keyed;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public interface FunctionalRecipe extends Keyed {

    RecipeMatcher getMatcher();

    RecipeAssembler getAssembler();

    RecipeRemainingItemsFunction getRemainingItemsFunction();

    default boolean matches(Inventory inventory, World world) {
        try {
            return getMatcher().match(inventory, world);
        } catch (RuntimeException e) {
            Bukkit.getLogger().severe("Error occurred when checking recipe! Removing " + getNamespacedKey() + "");
            e.printStackTrace();
            Bukkit.removeRecipe(getNamespacedKey().bukkit());
        }
        return false;
    }

    default Optional<ItemStack> assemble(Inventory inventory) {
        try {
            return getAssembler().assemble(inventory);
        } catch (RuntimeException e) {
            Bukkit.getLogger().severe("Error occurred when assembling recipe! Removing " + getNamespacedKey() + "");
            e.printStackTrace();
            Bukkit.removeRecipe(getNamespacedKey().bukkit());
        }
        return Optional.empty();
    }

    default Optional<List<ItemStack>> getRemainingItems(Inventory inventory) {
        try {
            return getRemainingItemsFunction().apply(inventory);
        } catch (RuntimeException e) {
            Bukkit.getLogger().severe("Error occurred when consuming recipe! Removing " + getNamespacedKey() + "");
            e.printStackTrace();
            Bukkit.removeRecipe(getNamespacedKey().bukkit());
        }
        return Optional.empty();
    }

}
