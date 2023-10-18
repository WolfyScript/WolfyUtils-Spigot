package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import com.wolfyscript.utilities.Copyable;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreImpl;
import com.wolfyscript.utilities.bukkit.world.items.references.APIReference;
import com.wolfyscript.utilities.common.WolfyCore;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;
import java.util.function.BiFunction;

@JsonDeserialize(using = StackReference.Deserializer.class)
public class StackReference implements Copyable<StackReference> {

    private final WolfyCore core;
    private final int customAmount;
    private final double weight;
    private final StackIdentifierParser<?> parser;
    /**
     * Used to store the original stack
     */
    private final ItemStack stack;
    /**
     * Used to store the previous parser result
     */
    private final StackIdentifier identifier;

    public StackReference(WolfyCore core, NamespacedKey parser, double weight, int customAmount, ItemStack item) {
        this.customAmount = customAmount;
        this.weight = weight;
        this.core = core;
        this.parser = core.getRegistries().getStackIdentifierParsers().get(parser);
        this.stack = item;
        this.identifier = parseIdentifier();
    }

    public StackReference(WolfyCore core, @NotNull StackIdentifierParser<?> parser, double weight, int customAmount, ItemStack item) {
        this.customAmount = customAmount;
        this.weight = weight;
        this.core = core;
        this.parser = parser;
        this.stack = item;
        this.identifier = parseIdentifier();
    }

    public StackReference(WolfyCore core, @NotNull StackIdentifier identifier, double weight, int customAmount, ItemStack item) {
        this.customAmount = customAmount;
        this.weight = weight;
        this.core = core;
        this.parser = identifier.parser();
        this.stack = item;
        this.identifier = identifier;
    }

    private StackReference(StackReference stackReference) {
        this.weight = stackReference.weight;
        this.customAmount = stackReference.customAmount;
        this.core = stackReference.core;
        this.parser = stackReference.parser;
        this.stack = stackReference.stack;
        this.identifier = parseIdentifier();
    }

    private StackIdentifier parseIdentifier() {
        Optional<? extends StackIdentifier> identifierOptional = parser.from(stack);
        if (identifierOptional.isPresent()) return identifierOptional.get();
        return new BukkitStackIdentifier(stack);
    }

    public StackIdentifier identifier() {
        return identifier;
    }

    public boolean matches(ItemStack other) {
        return matches(other, true, false);
    }

    public boolean matches(ItemStack other, boolean exact) {
        return matches(other, exact, false);
    }

    public boolean matches(ItemStack other, boolean exact, boolean ignoreAmount) {
        return identifier().matches(other, customAmount, exact, ignoreAmount);
    }

    @JsonGetter("weight")
    public double weight() {
        return weight;
    }

    @JsonGetter("amount")
    public int amount() {
        return customAmount;
    }

    @JsonIgnore
    public int effectiveAmount() {
        return amount() * stack().getAmount();
    }

    /**
     * Gets the <b>ORIGINAL</b> stack, from which this reference was created from!<br>
     * For the linked stack from for example an external plugin use {@link #identifier()}!
     *
     * @return The <b>ORIGINAL</b> stack this reference was created from
     * @see #identifier() Get the externally linked stack of this reference!
     */
    @JsonGetter("stack")
    public ItemStack stack() {
        return stack;
    }

    public StackIdentifierParser<?> parser() {
        return parser;
    }

    @JsonGetter("parser")
    private NamespacedKey parserId() {
        return parser.getNamespacedKey();
    }

    @Override
    public StackReference copy() {
        return new StackReference(this);
    }

    @Deprecated
    public APIReference convert() {
        return identifier().convert(weight, customAmount);
    }

    /**
     * Removes the specified amount from the input ItemStack inside an inventory!
     * <p>
     * This method will directly edit the input ItemStack (Change it's type, amount, etc.) and <b>won't return a result value!</b>
     *
     * <p>
     * <b>Stackable</b>  ({@linkplain Material#getMaxStackSize()} > 1 or input ItemStack amount > 1)<b>:</b><br>
     * The amount removed from the ItemStack is: <strong><code>totalAmount * {@link #amount()} * {@link #stack()}.{@link ItemStack#getAmount() getAmount()}</code></strong>
     * <p>
     * If the stack has craft remains:
     * <ul>
     *     <li><b>Player is not null: </b>Tries to add the item/s to the players inventory. If there is no space it will drop the item at the position of the player.</li>
     *     <li><b>Player is null:</b>
     *          <ul>
     *              <li>
     *                  <b>Location is null, Inventory is not null:</b> Tries to add the item/s to the inventory.<br>
     *                  If there is no space, it tries to get the location of the inventory to drop the item/s there instead.<br>
     *                  In case the inventory has no location, the item/s are not dropped and will be lost! Be careful with this!
     *              </li>
     *              <li><b>Location is not null: </b>Drops the items at that location.</li>
     *              <li><b>Location and Inventory is null: </b>Item/s are neither added to an inventory or dropped.</li>
     *          </ul>
     *     </li>
     * </ul>
     * </p>
     * </p>
     *     <p>
     *         <b>Un-stackable</b>  ({@linkplain ItemStack#getMaxStackSize()} == 1 and input ItemStack amount == 1)<b>:</b><br>
     *         This method will redirect to the {@link #removeUnStackableItem(ItemStack, boolean)} method.<br>
     *     </p>
     * </p>
     * <br>
     *
     * @param input              The input ItemStack, that is also going to be edited.
     * @param count              The amount of this reference that should be removed from the input.
     * @param inventory          The optional inventory to add the replacements to. (Only for stackable items)
     * @param player             The player to give the items to. If the players' inventory has space the craft remains are added. (Only for stackable items)
     * @param location           The location where the replacements should be dropped. (Only for stackable items)
     * @param replaceWithRemains If the Item should be replaced by the default craft remains (Only for un-stackable items).
     */
    public void remove(ItemStack input, int count, @Nullable Inventory inventory, @Nullable Player player, @Nullable Location location, boolean replaceWithRemains) {
        if (stack().getMaxStackSize() == 1 && input.getAmount() == 1) {
            removeUnStackableItem(input, replaceWithRemains);
        } else {
            int amount = input.getAmount() - effectiveAmount() * count;
            input.setAmount(amount);
            applyStackableReplacement(count, replaceWithRemains, player, inventory, location);
        }
    }

    private void applyStackableReplacement(int totalAmount, boolean useRemains, @Nullable Player player, @Nullable Inventory inventory, @Nullable Location location) {
        CustomItem.craftRemain(stack())
                .map(material -> useRemains ? new ItemStack(material) : null)
                .ifPresent(replacement -> {
                    Location loc = location;
                    replacement.setAmount(replacement.getAmount() * totalAmount);
                    if (player != null) {
                        var playerInv = player.getInventory();
                        if (InventoryUtils.hasInventorySpace(playerInv, replacement)) {
                            playerInv.addItem(replacement);
                            return;
                        }
                        loc = player.getLocation();
                    }
                    if (loc == null) {
                        if (inventory == null) return;
                        if (InventoryUtils.hasInventorySpace(inventory, replacement)) {
                            inventory.addItem(replacement);
                            return;
                        }
                        loc = inventory.getLocation();
                    }
                    if (loc != null && loc.getWorld() != null) {
                        loc.getWorld().dropItemNaturally(loc.add(0.5, 1.0, 0.5), replacement);
                    }
                });
    }

    /**
     * Removes the specified amount from the input ItemStack inside an inventory!
     * <p>
     * This method will directly edit the input ItemStack (Change it's type, amount, etc.) and won't return a result value!
     *
     * <p>
     * <b>Stackable</b>  ({@linkplain Material#getMaxStackSize()} > 1 or input ItemStack amount > 1)<b>:</b><br>
     * The amount removed from the ItemStack is: <strong><code>totalAmount * {@link #amount()} * {@link #stack()}.{@link ItemStack#getAmount() getAmount()}</code></strong>
     * <p>
     * If the custom item has a replacement/craft remain:
     * <ul>
     *      <li>
     *          <b>Location is null, Inventory is not null:</b> Tries to add the item/s to the inventory.<br>
     *          If there is no space, it tries to get the location of the inventory to drop the item/s there instead.<br>
     *          In case the inventory has no location, the item/s are not dropped and will be lost! Be careful with this!
     *      </li>
     *      <li><b>Location is not null: </b>Drops the items at that location.</li>
     *      <li><b>Location and Inventory is null: </b>Item/s are neither added to an inventory or dropped.</li>
     * </ul>
     * </p>
     * </p>
     *     <p>
     *         <b>Un-stackable</b>  ({@linkplain Material#getMaxStackSize()} == 1 and input ItemStack amount == 1)<b>:</b><br>
     *         This method will redirect to the {@link #removeUnStackableItem(ItemStack, boolean)} method.<br>
     *     </p>
     * </p>
     * <br>
     *
     * @param input              The input ItemStack, that is also going to be edited.
     * @param totalAmount        The amount of this custom item that should be removed from the input.
     * @param inventory          The optional inventory to add the replacements to. (Only for stackable items)
     * @param location           The location where the replacements should be dropped. (Only for stackable items)
     * @param replaceWithRemains If the Item should be replaced by the default craft remains (Only for un-stackable items).
     */
    public void remove(ItemStack input, int totalAmount, Inventory inventory, Location location, boolean replaceWithRemains) {
        remove(input, totalAmount, inventory, null, location, replaceWithRemains);
    }

    /**
     * Removes the specified amount from the input ItemStack inside an inventory!
     * <p>
     * This method will directly edit the input ItemStack (Change it's type, amount, etc.) and won't return a result value!
     *
     * <p>
     * <b>Stackable</b>  ({@linkplain Material#getMaxStackSize()} > 1 or input ItemStack amount > 1)<b>:</b><br>
     * The amount removed from the input ItemStack is equals to <strong><code>{@link #getAmount()} * totalAmount</code></strong>
     * <p>
     * If the custom item has a replacement/craft remain:
     * <ul>
     *      <li>
     *          <b>Location is null, Inventory is not null:</b> Tries to add the item/s to the inventory.<br>
     *          If there is no space, it tries to get the location of the inventory to drop the item/s there instead.<br>
     *          In case the inventory has no location, the item/s are not dropped and will be lost! Be careful with this!
     *      </li>
     *      <li><b>Location is not null: </b>Drops the items at that location.</li>
     *      <li><b>Location and Inventory is null: </b>Item/s are neither added to an inventory or dropped.</li>
     * </ul>
     * </p>
     * </p>
     *     <p>
     *         <b>Un-stackable</b>  ({@linkplain Material#getMaxStackSize()} == 1 and input ItemStack amount == 1)<b>:</b><br>
     *         This method will redirect to the {@link #removeUnStackableItem(ItemStack, boolean)} method.<br>
     *     </p>
     * </p>
     * <br>
     *
     * @param input       The input ItemStack, that is also going to be edited.
     * @param totalAmount The amount of this custom item that should be removed from the input.
     * @param inventory   The optional inventory to add the replacements to. (Only for stackable items)
     * @param location    The location where the replacements should be dropped. (Only for stackable items)
     * @see #remove(ItemStack, int, Inventory, Location, boolean)
     */
    public void remove(ItemStack input, int totalAmount, Inventory inventory, Location location) {
        remove(input, totalAmount, inventory, location, true);
    }

    /**
     * Removes the specified amount from the input ItemStack inside an inventory!
     * <p>
     * This method will directly edit the input ItemStack (Change it's type, amount, etc.) and won't return a result value!
     *
     * <p>
     * <b>Stackable</b>  ({@linkplain Material#getMaxStackSize()} > 1 or input ItemStack amount > 1)<b>:</b><br>
     * The amount removed from the input ItemStack is equals to <strong><code>{@link #getAmount()} * totalAmount</code></strong>
     * <p>
     * If the custom item has a replacement/craft remain:
     * <ul>
     *      <li>
     *          <b>Inventory is not null:</b> Tries to add the item/s to the inventory.<br>
     *          If there is no space, it tries to get the location of the inventory to drop the item/s there instead.<br>
     *          In case the inventory has no location, the item/s are not dropped and will be lost! Be careful with this!
     *      </li>
     *      <li><b>Inventory is null: </b>Item/s are neither added to an inventory or dropped.</li>
     * </ul>
     * </p>
     * </p>
     *     <p>
     *         <b>Un-stackable</b>  ({@linkplain Material#getMaxStackSize()} == 1 and input ItemStack amount == 1)<b>:</b><br>
     *         This method will redirect to the {@link #removeUnStackableItem(ItemStack, boolean)} method.<br>
     *     </p>
     * </p>
     * <br>
     *
     * @param input       The input ItemStack, that is also going to be edited.
     * @param totalAmount The amount of this custom item that should be removed from the input.
     * @param inventory   The optional inventory to add the replacements to. (Only for stackable items)
     * @see #remove(ItemStack, int, Inventory, Location, boolean)
     */
    public void remove(ItemStack input, int totalAmount, Inventory inventory) {
        remove(input, totalAmount, inventory, null);
    }

    /**
     * Removes the specified amount from the input ItemStack inside a inventory!
     * <p>
     * This method will directly edit the input ItemStack and won't return a result value.
     *
     * <p>
     * <strong>Stackable:</strong><br>
     * The amount removed from the input ItemStack is equals to <strong><code>{@link #getAmount()} * totalAmount</code></strong>
     * <p>
     * If the custom item has a replacement:
     * <ul>
     *     <li><b>If location is not null,</b> then it will drop the items at that location.</li>
     *     <li><b>If location is null,</b> then the replacement items are neither dropped nor added to the inventory!</li>
     * </ul>
     * </p>
     * </p>
     *     <p>
     *         <strong>Un-stackable:</strong><br>
     *         This method will redirect to the {@link #removeUnStackableItem(ItemStack)} method and replaces the item with it's craft remains if available.
     *     </p>
     * </p>
     * <br>
     *
     * @param input       The input ItemStack, that is also going to be edited.
     * @param totalAmount The amount of this custom item that should be removed from the input.
     * @param location    The location where the replacements should be dropped. (Only for stackable items)
     * @return The original input {@link ItemStack} that was directly edited by the method.
     * @see #remove(ItemStack, int, Inventory, Location, boolean)
     */
    public ItemStack remove(ItemStack input, int totalAmount, Location location) {
        remove(input, totalAmount, null, location);
        return input;
    }

    /**
     * Removes the input as an un-stackable item.
     * <p>
     * Items that have craft remains by default will be replaced with the according {@link Material} <br>
     * Like Buckets, Potions, Stew/Soup.
     * </p>
     * <p>
     * If this CustomItem has a custom replacement then the input will be replaced with that.
     * </p>
     * <br>
     *
     * @param input The input ItemStack, that is going to be edited.
     */
    public void removeUnStackableItem(ItemStack input) {
        removeUnStackableItem(input, true);
    }

    /**
     * Removes the input as an un-stackable item.
     * <p>
     * Items that have craft remains by default will be replaced with the according {@link Material} <br>
     * Like Buckets, Potions, Stew/Soup.
     * </p>
     * <p>
     * If this CustomItem has a custom replacement then the input will be replaced with that.
     * </p>
     * <br>
     *
     * @param input      The input ItemStack, that is going to be edited.
     * @param useRemains If the item should be replaced by it's remains if removed. Not including custom replacement options!
     */
    public void removeUnStackableItem(ItemStack input, boolean useRemains) {
        CustomItem.craftRemain(stack()).ifPresentOrElse(craftRemain -> {
            if (useRemains) {
                input.setType(craftRemain);
                input.setItemMeta(Bukkit.getItemFactory().getItemMeta(craftRemain));
                return;
            }
            input.setAmount(0);
        }, () -> input.setAmount(0));
    }

    /**
     * Shrinks the specified stack by the given amount and returns the manipulated or replaced item!
     * <p>
     * <p>
     * <b>Stackable</b>  ({@linkplain Material#getMaxStackSize()} > 1 or stack count > 1)<b>:</b><br>
     * The stack is shrunk by the specified amount (<strong><code>{@link #getAmount()} * totalAmount</code></strong>)
     * <p>
     * If this CustomItem has a custom replacement:<br>
     * This calls the stackReplacement function with the shrunken stack and this CustomItem.
     * It is meant for applying the stackable replacement items.<br>
     * For default behaviour see {@link #shrink(ItemStack, int, boolean, Inventory, Player, Location)} and {@link #shrinkUnstackableItem(ItemStack, boolean)}
     * </p>
     * </p>
     * <p>
     * <b>Un-stackable</b>  ({@linkplain Material#getMaxStackSize()} == 1 and stack count == 1)<b>:</b><br>
     * Redirects to {@link #removeUnStackableItem(ItemStack, boolean)}<br>
     * </p>
     * </p>
     * <br>
     *
     * @param stack            The input ItemStack, that is also going to be edited.
     * @param count            The amount of this custom item that should be removed from the input.
     * @param useRemains       If the Item should be replaced by the default craft remains.
     * @param stackReplacement Behaviour of how to apply the replacements of stackable items.
     * @return The manipulated stack, default remain, or custom remains.
     */
    public ItemStack shrink(@NotNull ItemStack stack, int count, boolean useRemains, @NotNull BiFunction<StackReference, ItemStack, ItemStack> stackReplacement) {
        if (stack().getMaxStackSize() == 1 && stack.getAmount() == 1) {
            return shrinkUnstackableItem(stack, useRemains);
        }
        int amount = stack.getAmount() - (effectiveAmount() * count);
        if (amount <= 0) {
            stack = new ItemStack(Material.AIR);
        } else {
            stack.setAmount(amount);
        }
        return stackReplacement.apply(this, stack);
    }

    /**
     * Shrinks the specified stack by the given amount and returns the manipulated or replaced item!
     * <p>
     * <b>Stackable</b>  ({@linkplain Material#getMaxStackSize()} > 1 or stack count > 1)<b>:</b><br>
     * The stack is shrunk by the specified amount (<strong><code>{@link #getAmount()} * totalAmount</code></strong>)
     * <p>
     * If this CustomItem has a custom replacement:<br>
     * <ul>
     *   <li><b>Location: </b>Used as the drop location for remaining items. <br>May be overridden by options below.</li>
     *   <li>
     *     <b>Player: </b>Adds items to the players inventory.
     *     <br>Remaining items are still in the pool for the next options below.
     *     <br>Player location is used as the drop location for remaining items.</li>
     *   <li>
     *     <b>Inventory:</b> Adds items to the inventory.
     *     <br>Remaining items are still in the pool for the next options below.
     *     <br>If location not available yet: uses inventory location as drop location for remaining items.
     *   </li>
     * </ul>
     * All remaining items that cannot be added to player or the other inventory are dropped at the specified location.<br>
     * <b>Warning! If you do not provide a location via <code>player</code>, <code>inventory</code>, or <code>inventory</code>, then the remaining items are discarded!</b><br>
     * For custom behaviour see {@link #shrink(ItemStack, int, boolean, BiFunction)}.
     * </p>
     * </p>
     * <p>
     * <b>Un-stackable</b>  ({@linkplain Material#getMaxStackSize()} == 1 and stack count == 1)<b>:</b><br>
     * Redirects to {@link #removeUnStackableItem(ItemStack, boolean)}<br>
     * </p>
     * </p>
     * <br>
     *
     * @param stack      The input ItemStack, that is also going to be edited.
     * @param count      The amount of this custom item that should be removed from the input.
     * @param useRemains If the Item should be replaced by the default craft remains.
     * @param inventory  The optional inventory to add the replacements to. (Only for stackable items)
     * @param player     The player to give the items to. If the players' inventory has space the craft remains are added. (Only for stackable items)
     * @param location   The location where the replacements should be dropped. (Only for stackable items)
     * @return The manipulated stack, default remain, or custom remains.
     */
    public ItemStack shrink(ItemStack stack, int count, boolean useRemains, @Nullable final Inventory inventory, @Nullable final Player player, @Nullable final Location location) {
        return shrink(stack, count, useRemains, (customItem, resultStack) -> CustomItem.craftRemain(stack())
                .map(material -> useRemains ? new ItemStack(material) : null)
                .map(replacement -> {
                    var originalStack = resultStack;
                    int replacementAmount = count;
                    if (ItemUtils.isAirOrNull(originalStack)) {
                        int returnableAmount = Math.min(replacement.getMaxStackSize(), replacementAmount);
                        replacementAmount -= returnableAmount;
                        originalStack = replacement.clone();
                        originalStack.setAmount(replacementAmount);
                    }
                    if (replacementAmount > 0) {
                        replacement.setAmount(replacementAmount);
                        Location loc = location;
                        if (player != null) {
                            replacement = player.getInventory().addItem(replacement).get(0);
                            loc = player.getLocation();
                        }
                        if (inventory != null && replacement != null) {
                            replacement = inventory.addItem(replacement).get(0);
                            if (loc == null) loc = inventory.getLocation();
                        }
                        if (loc != null && replacement != null && loc.getWorld() != null) {
                            loc.getWorld().dropItemNaturally(loc.add(0.5, 1.0, 0.5), replacement);
                        }
                    }
                    return originalStack;
                }).orElse(new ItemStack(Material.AIR)));
    }

    /**
     * Shrinks the specified stack and returns the manipulated or replaced item!
     * <p>
     *     This firstly checks for custom replacements (remains) and sets it as the result.<br>
     *     Then handles damaging of the stack, if there is a specified durability cost.<br>
     *     In case the stack breaks due damage it is replaced by the result, specified earlier.
     * </p>
     *
     * @param stack      The stack to shrink
     * @param useRemains If the Item should be replaced by the default craft remains.
     * @return The manipulated (damaged) stack, default remain, or custom remains.
     */
    public ItemStack shrinkUnstackableItem(ItemStack stack, boolean useRemains) {
        return CustomItem.craftRemain(stack()).map(material -> useRemains ? new ItemStack(material) : null).orElse(new ItemStack(Material.AIR));
    }

    /**
     *
     */
    public static class Deserializer extends StdNodeBasedDeserializer<StackReference> {

        private final WolfyCore core;

        protected Deserializer() {
            super(StackReference.class);
            this.core = WolfyCoreImpl.getInstance();
        }

        @Override
        public StackReference convert(JsonNode root, DeserializationContext ctxt) throws IOException {
            if (root.has("type")) {
                // New ItemReference used! No conversion required!
                return new StackReference(core,
                        ctxt.readTreeAsValue(root.get("parser"), NamespacedKey.class),
                        root.get("weight").asDouble(1),
                        root.get("amount").asInt(1),
                        ctxt.readTreeAsValue(root.get("stack"), ItemStack.class)
                );
            }
            // Need to convert APIReference
            APIReference apiReference = ctxt.readTreeAsValue(root, APIReference.class);
            return apiReference.convertToStackReference();
        }
    }
}
