package me.wolfyscript.utilities.api.nms.item.crafting;

import me.wolfyscript.utilities.util.Keyed;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface FunctionalRecipe extends Keyed {

    BiFunction<Inventory, World, Boolean> getMatcher();

    Function<Inventory, Optional<ItemStack>> getAssembler();

    void setAssembler(Function<Inventory, Optional<ItemStack>> assembler);

    Function<Inventory, Optional<List<ItemStack>>> getRemainingItemsFunction();

    void setRemainingItemsFunction(Function<Inventory, Optional<List<ItemStack>>> function);

    default boolean matches(Inventory inventory, World world) {
        try {
            return getMatcher().apply(inventory, world);
        } catch (RuntimeException e) {
            Bukkit.getLogger().severe("Error occurred when checking recipe! Removing " + getNamespacedKey() + "");
            Bukkit.removeRecipe(getNamespacedKey().bukkit());
        }
        return false;
    }

    default Optional<ItemStack> assemble(Inventory inventory) {
        try {
            return getAssembler().apply(inventory);
        } catch (RuntimeException e) {
            Bukkit.getLogger().severe("Error occurred when assembling recipe! Removing " + getNamespacedKey() + "");
            Bukkit.removeRecipe(getNamespacedKey().bukkit());
        }
        return Optional.empty();
    }

    default Optional<List<ItemStack>> getRemainingItems(Inventory inventory) {
        try {
            return getRemainingItemsFunction().apply(inventory);
        } catch (RuntimeException e) {
            Bukkit.getLogger().severe("Error occurred when consuming recipe! Removing " + getNamespacedKey() + "");
            Bukkit.removeRecipe(getNamespacedKey().bukkit());
        }
        return Optional.empty();
    }


}
