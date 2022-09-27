package com.wolfyscript.utilities.bukkit.nms.item.crafting;

import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class FunctionalRecipeBuilderBlasting extends FunctionalRecipeBuilderCooking {

    public FunctionalRecipeBuilderBlasting(NamespacedKey key, ItemStack result, RecipeChoice ingredient) {
        super(key, result, ingredient);
    }

    @Override
    protected FunctionalRecipeType getType() {
        return FunctionalRecipeType.BLASTING;
    }
}
