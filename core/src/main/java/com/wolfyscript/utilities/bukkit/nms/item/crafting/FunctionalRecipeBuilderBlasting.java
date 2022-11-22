package com.wolfyscript.utilities.bukkit.nms.item.crafting;

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class FunctionalRecipeBuilderBlasting extends FunctionalRecipeBuilderCooking {

    public FunctionalRecipeBuilderBlasting(BukkitNamespacedKey key, ItemStack result, RecipeChoice ingredient) {
        super(key, result, ingredient);
    }

    @Override
    protected FunctionalRecipeType getType() {
        return FunctionalRecipeType.BLASTING;
    }
}
