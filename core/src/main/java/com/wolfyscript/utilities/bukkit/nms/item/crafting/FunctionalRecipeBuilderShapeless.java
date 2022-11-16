package com.wolfyscript.utilities.bukkit.nms.item.crafting;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class FunctionalRecipeBuilderShapeless extends FunctionalRecipeBuilderCrafting {

    private List<RecipeChoice> choices = new ArrayList<>();

    public FunctionalRecipeBuilderShapeless(NamespacedKey key, ItemStack result) {
        super(key, result);
    }

    public void setChoices(List<RecipeChoice> choices) {
        this.choices = choices == null ? new ArrayList<>() : choices;
    }

    public List<RecipeChoice> getChoices() {
        return choices;
    }

    @Override
    protected FunctionalRecipeType getType() {
        return FunctionalRecipeType.CRAFTING_SHAPELESS;
    }

    @Override
    public void createAndRegister() {
        try {
            Constructor<?> constructor = FunctionalRecipeGenerator.getFunctionalRecipeClass(getType()).getConstructor(
                    NamespacedKey.class, RecipeMatcher.class, RecipeAssembler.class, RecipeRemainingItemsFunction.class, String.class, ItemStack.class, List.class
            );
            FunctionalRecipe<CraftingInventory> recipe = (FunctionalRecipe<CraftingInventory>) constructor.newInstance(key, recipeMatcher, recipeAssembler, remainingItemsFunction, group, result, choices);
            FunctionalRecipeGenerator.addRecipeToRecipeManager(recipe);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
