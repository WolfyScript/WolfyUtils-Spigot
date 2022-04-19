package me.wolfyscript.utilities.api.nms.inventory;

import com.google.common.base.Preconditions;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class ExtendedRecipeChoice {

    private final Function<ItemStack, Boolean> sourceCheck;
    private List<ItemStack> choices;

    protected ExtendedRecipeChoice(Function<ItemStack, Boolean> sourceCheck, @NotNull ItemStack stack) {
        this(sourceCheck, List.of(stack));
    }

    protected ExtendedRecipeChoice(Function<ItemStack, Boolean> sourceCheck, @NotNull ItemStack... stacks) {
        this(sourceCheck, Arrays.asList(stacks));
    }

    protected ExtendedRecipeChoice(Function<ItemStack, Boolean> sourceCheck, @NotNull List<ItemStack> choices) {
        Preconditions.checkArgument(choices != null, "choices");
        Preconditions.checkArgument(!choices.isEmpty(), "Must have at least one choice");
        for (ItemStack choice : choices) {
            Preconditions.checkArgument(choice != null, "Cannot have null choice");
        }
        this.sourceCheck = sourceCheck;
        this.choices = new ArrayList<>(choices);
    }

    @NotNull
    public ItemStack getItemStack() {
        return choices.get(0).clone();
    }

    @NotNull
    public List<ItemStack> getChoices() {
        return Collections.unmodifiableList(choices);
    }

    @NotNull
    @Override
    public ExtendedRecipeChoice clone() {
        try {
            ExtendedRecipeChoice clone = (ExtendedRecipeChoice) super.clone();
            clone.choices = new ArrayList<>(choices);
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError(ex);
        }
    }

    public boolean test(@NotNull ItemStack itemStack) {
        return sourceCheck.apply(itemStack);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.choices);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExtendedRecipeChoice other = (ExtendedRecipeChoice) obj;
        return Objects.equals(this.choices, other.choices);
    }

    @Override
    public String toString() {
        return "ExtendedChoice{" + "choices=" + choices + '}';
    }



}
