package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.wolfyscript.utilities.Copyable;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.json.KeyedTypeIdResolver;
import com.wolfyscript.utilities.json.KeyedTypeResolver;
import java.util.Optional;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = {"type"})
public abstract class ItemReference implements Keyed, Copyable<ItemReference> {

    protected final NamespacedKey type;

    protected WolfyUtils wolfyUtils;
    protected int amount;
    private double weight;

    protected ItemReference(WolfyUtils wolfyUtils) {
        this.wolfyUtils = wolfyUtils;
        this.type = wolfyUtils.getIdentifiers().getNamespaced(getClass());
    }

    protected ItemReference(ItemReference reference) {
        this.type = WolfyCoreBukkit.getInstance().getWolfyUtils().getIdentifiers().getNamespaced(getClass());
        this.amount = reference.amount;
        this.weight = reference.weight;
    }

    public abstract ItemStack getItem();

    public ItemStack getItem(Player player) {
        return getItem();
    }

    public ItemStack getItem(Block block) {
        return getItem();
    }

    public abstract boolean isValidItem(ItemStack itemStack);

    /**
     * The amount of the reference.
     * <p>
     * If the amount of this {@link ItemReference}:
     *     <ul>
     *         <li>is equal or less than 0, then this method will return the amount of the {@link ItemStack#getAmount()} from {@link #getItem()}.</li>
     *         <li>is greater than 0, then this method will return the amount</li>
     *     </ul>
     * <p>
     *
     * @return The correct amount of this reference or linked item.
     */
    public int getAmount() {
        return amount > 0 ? amount : getItem().getAmount();
    }

    /**
     * Sets the amount of this APIReference.
     * Note: That a value of 0 or less indicates that the amount of this APIReference is equal to the linked ItemStack.
     *
     * @param amount The amount of this APIReference.
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * The weight can be used to select an item randomly based on their weight.
     * For example for recipes like CustomCrafting does.
     * <br>
     * If the weight is bigger than 0 it will override the weight of the {@link CustomItem}, that is created from this reference.
     *
     * @return The weight of this reference.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * The weight can be used to select an item randomly based on their weight.
     * For example for recipes like CustomCrafting does.
     * <br>
     * If the weight is bigger than 0 it will override the weight of the {@link CustomItem}, that is created from this reference.
     *
     * @param weight The weight of this reference.
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return type;
    }

    @Override
    public String toString() {
        return "ItemReference{" +
                "type=" + type +
                ", amount=" + amount +
                ", weight=" + weight +
                '}';
    }

    public interface Parser<T extends ItemReference> {

        Optional<T> parseFromStack(WolfyUtils wolfyUtils, ItemStack stack);

    }

}
