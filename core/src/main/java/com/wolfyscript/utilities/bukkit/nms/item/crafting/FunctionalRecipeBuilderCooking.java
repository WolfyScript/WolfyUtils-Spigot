package com.wolfyscript.utilities.bukkit.nms.item.crafting;

import com.google.common.base.Preconditions;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public abstract class FunctionalRecipeBuilderCooking {

    private final NamespacedKey key;

    private RecipeMatcher recipeMatcher;
    private RecipeAssembler recipeAssembler;
    private RecipeRemainingItemsFunction remainingItemsFunction;

    private final ItemStack result;
    private final RecipeChoice ingredient;
    private float experience = 1f;
    private int cookingTime = 60;
    private String group = "";

    public FunctionalRecipeBuilderCooking(NamespacedKey key, ItemStack result, RecipeChoice ingredient) {
        this.key = key;
        this.result = result;
        this.ingredient = ingredient;
        this.recipeMatcher = (inventory, world) -> inventory.getItem(0) != null && ingredient.test(inventory.getItem(0));
        this.recipeAssembler = inventory -> Optional.empty();
        this.remainingItemsFunction = inventory -> Optional.empty();
    }

    public NamespacedKey getKey() {
        return key;
    }

    public RecipeMatcher getRecipeMatcher() {
        return recipeMatcher;
    }

    public void setRecipeMatcher(RecipeMatcher recipeMatcher) {
        Preconditions.checkNotNull(recipeMatcher);
        this.recipeMatcher = recipeMatcher;
    }

    public RecipeAssembler getRecipeAssembler() {
        return recipeAssembler;
    }

    public void setRecipeAssembler(RecipeAssembler recipeAssembler) {
        Preconditions.checkNotNull(recipeAssembler);
        this.recipeAssembler = recipeAssembler;
    }

    public RecipeRemainingItemsFunction getRemainingItemsFunction() {
        return remainingItemsFunction;
    }

    public void setRemainingItemsFunction(RecipeRemainingItemsFunction remainingItemsFunction) {
        Preconditions.checkNotNull(remainingItemsFunction);
        this.remainingItemsFunction = remainingItemsFunction;
    }

    public ItemStack getResult() {
        return result;
    }

    public RecipeChoice getIngredient() {
        return ingredient;
    }

    public float getExperience() {
        return experience;
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        Preconditions.checkArgument(cookingTime <= Short.MAX_VALUE, "The cooking time cannot be longer than 32767 ticks!");
        this.cookingTime = cookingTime;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group == null ? "" : group;
    }

    protected abstract FunctionalRecipeType getType();

    public void createAndRegister() {
        try {
            Constructor<?> constructor = FunctionalRecipeGenerator.getFunctionalRecipeClass(getType()).getConstructor(
                    NamespacedKey.class, RecipeMatcher.class, RecipeAssembler.class, RecipeRemainingItemsFunction.class, String.class, RecipeChoice.class, ItemStack.class, Float.TYPE, Integer.TYPE
            );
            FunctionalRecipe recipe = (FunctionalRecipe) constructor.newInstance(key, recipeMatcher, recipeAssembler, remainingItemsFunction, group, ingredient, result, experience, cookingTime);
            FunctionalRecipeGenerator.addRecipeToRecipeManager(recipe);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
