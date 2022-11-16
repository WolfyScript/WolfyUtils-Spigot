package com.wolfyscript.utilities.bukkit.nms.item.crafting;

import java.util.Optional;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public abstract class FunctionalRecipeBuilderCrafting extends FunctionalRecipeBuilder<CraftingInventory> {

    protected ItemStack result;

    public FunctionalRecipeBuilderCrafting(NamespacedKey key, ItemStack result) {
        super(key);
        this.result = result;
        this.recipeMatcher = null;
        this.recipeAssembler = inventory -> Optional.empty();
        this.remainingItemsFunction = inventory -> Optional.empty();
    }

    public ItemStack getResult() {
        return result;
    }

    public NamespacedKey getKey() {
        return key;
    }

    protected abstract FunctionalRecipeType getType();


}
