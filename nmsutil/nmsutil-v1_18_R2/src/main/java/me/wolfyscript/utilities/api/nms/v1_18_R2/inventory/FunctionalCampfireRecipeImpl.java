package me.wolfyscript.utilities.api.nms.v1_18_R2.inventory;

import me.wolfyscript.utilities.api.nms.inventory.FunctionalCampfireRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;

import java.util.function.BiFunction;

public class FunctionalCampfireRecipeImpl extends CampfireCookingRecipe implements FunctionalCampfireRecipe {

    private final BiFunction<Inventory, World, Boolean> recipeMatch;

    public FunctionalCampfireRecipeImpl(ResourceLocation key, String group, Ingredient ingredient, ItemStack result, float experience, int cookingTime, BiFunction<Inventory, World, Boolean> recipeMatch) {
        super(key, group, ingredient, result, experience, cookingTime);
        this.recipeMatch = recipeMatch;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return matches(new CraftInventory(container), level.getWorld());
    }

    @Override
    public BiFunction<Inventory, World, Boolean> getMatcher() {
        return recipeMatch;
    }
}
