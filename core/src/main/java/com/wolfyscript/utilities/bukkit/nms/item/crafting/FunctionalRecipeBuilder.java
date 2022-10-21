package com.wolfyscript.utilities.bukkit.nms.item.crafting;

import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

public abstract class FunctionalRecipeBuilder<T extends Inventory> {

    protected final NamespacedKey key;

    protected RecipeMatcher<T> recipeMatcher;
    protected RecipeAssembler<T> recipeAssembler;
    protected RecipeRemainingItemsFunction<T> remainingItemsFunction;

    protected String group = "";

    public FunctionalRecipeBuilder(NamespacedKey key) {
        this.key = key;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public RecipeMatcher<T> getRecipeMatcher() {
        return recipeMatcher;
    }

    public void setRecipeMatcher(@Nullable RecipeMatcher<T> recipeMatcher) {
        this.recipeMatcher = recipeMatcher;
    }

    public RecipeAssembler<T> getRecipeAssembler() {
        return recipeAssembler;
    }

    public void setRecipeAssembler(RecipeAssembler<T> recipeAssembler) {
        Preconditions.checkNotNull(recipeAssembler);
        this.recipeAssembler = recipeAssembler;
    }

    public RecipeRemainingItemsFunction<T> getRemainingItemsFunction() {
        return remainingItemsFunction;
    }

    public void setRemainingItemsFunction(RecipeRemainingItemsFunction<T> remainingItemsFunction) {
        Preconditions.checkNotNull(remainingItemsFunction);
        this.remainingItemsFunction = remainingItemsFunction;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group == null ? "" : group;
    }

    public abstract void createAndRegister();
}
