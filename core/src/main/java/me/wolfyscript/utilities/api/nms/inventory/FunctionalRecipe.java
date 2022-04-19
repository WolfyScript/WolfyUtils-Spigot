package me.wolfyscript.utilities.api.nms.inventory;

import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface FunctionalRecipe {

    BiFunction<Inventory, World, Boolean> getMatcher();

    Function<Inventory, Optional<ItemStack>> getAssembler();

    void setAssembler(Function<Inventory, Optional<ItemStack>> assembler);

    Function<Inventory, Optional<List<ItemStack>>> getRemainingItemsFunction();

    void setRemainingItemsFunction(Function<Inventory, Optional<List<ItemStack>>> function);

    default boolean matches(Inventory inventory, World world) {
        return getMatcher().apply(inventory, world);
    }

    default Optional<ItemStack> assemble(Inventory inventory) {
        return getAssembler().apply(inventory);
    }

    default Optional<List<ItemStack>> getRemainingItems(Inventory inventory) {
        return getRemainingItemsFunction().apply(inventory);
    }


}
