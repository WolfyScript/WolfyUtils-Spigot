package me.wolfyscript.utilities.api.nms.v1_18_R2.item.crafting;

import me.wolfyscript.utilities.api.nms.item.crafting.FunctionalRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;

import java.util.Optional;

public interface FunctionalRecipeInternals extends FunctionalRecipe {

    default Optional<NonNullList<ItemStack>> calcRemainingItems(Container container) {
        return getRemainingItems(new CraftInventory(container)).map(itemStacks -> {
            NonNullList<ItemStack> items = NonNullList.createWithCapacity(itemStacks.size());
            for (int i = 0; i < itemStacks.size(); i++) {
                items.set(i, CraftItemStack.asNMSCopy(itemStacks.get(i)));
            }
            return items;
        });
    }

    default Optional<ItemStack> assembleResult(Container container) {
        return assemble(new CraftInventory(container)).map(CraftItemStack::asNMSCopy);
    }

}
