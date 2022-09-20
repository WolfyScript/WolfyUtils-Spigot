package me.wolfyscript.utilities.api.nms.item.crafting;

import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class FunctionalRecipeBuilderSmoking extends FunctionalCookingRecipeBuilder{

    public FunctionalRecipeBuilderSmoking(NamespacedKey key, ItemStack result, RecipeChoice ingredient) {
        super(key, result, ingredient);
    }

    @Override
    protected FunctionalRecipeType getType() {
        return FunctionalRecipeType.SMOKING;
    }
}
