package me.wolfyscript.utilities.api.nms.v1_18_R2.inventory;

import me.wolfyscript.utilities.api.nms.inventory.ExtendedRecipeChoice;
import net.minecraft.world.item.crafting.Ingredient;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ExtendedRecipeChoiceImpl extends ExtendedRecipeChoice {

    public ExtendedRecipeChoiceImpl(Function<ItemStack, Boolean> sourceCheck, @NotNull ItemStack stack) {
        this(sourceCheck, List.of(stack));
    }

    public ExtendedRecipeChoiceImpl(Function<ItemStack, Boolean> sourceCheck, @NotNull ItemStack... stacks) {
        this(sourceCheck, Arrays.asList(stacks));
    }

    public ExtendedRecipeChoiceImpl(Function<ItemStack, Boolean> sourceCheck, @NotNull List<ItemStack> choices) {
        super(sourceCheck, choices);
    }

    public Ingredient toNMS() {
        Ingredient stack = new Ingredient(getChoices().stream().map((mat) -> new Ingredient.ItemValue(CraftItemStack.asNMSCopy(mat))));
        stack.exact = true;
        stack.dissolve();
        if (stack.itemStacks != null && stack.itemStacks.length == 0) {
            throw new IllegalArgumentException("Recipe requires at least one non-air choice!");
        } else {
            return stack;
        }
    }

}
