package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import com.wolfyscript.utilities.Copyable;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@JsonDeserialize(using = StackReference.Deserializer.class)
public class StackReference implements Copyable<StackReference> {

    private final WolfyUtilCore core;
    private final int customAmount;
    private final double weight;
    /**
     * Used to store the original stack
     */
    private final ItemStack stack;
    /**
     * Used to store the previous parser result
     */
    private final StackIdentifier identifier;
    private StackIdentifierParser<?> parser;

    public static StackReference of(ItemStack itemStack) {
        return new StackReference(WolfyUtilCore.getInstance(), new BukkitStackIdentifier(itemStack), 1, 1, itemStack);
    }

    public StackReference(WolfyUtilCore core, NamespacedKey parser, double weight, int customAmount, ItemStack item) {
        this.customAmount = customAmount;
        this.weight = weight;
        this.core = core;
        this.parser = core.getRegistries().getStackIdentifierParsers().get(parser);
        this.stack = item;
        this.identifier = parseIdentifier();
    }

    public StackReference(WolfyUtilCore core, @NotNull StackIdentifierParser<?> parser, double weight, int customAmount, ItemStack item) {
        this.customAmount = customAmount;
        this.weight = weight;
        this.core = core;
        this.parser = parser;
        this.stack = item;
        this.identifier = parseIdentifier();
    }

    public StackReference(WolfyUtilCore core, @NotNull StackIdentifier identifier, double weight, int customAmount, ItemStack item) {
        this.customAmount = customAmount;
        this.weight = weight;
        this.core = core;
        this.parser = core.getRegistries().getStackIdentifierParsers().get(identifier.getNamespacedKey());
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

    /**
     * Convenience method to get the stack the identifier points to.<br>
     * This is the same as <code>{@link #identifier() identifier()}.{@link StackIdentifier#stack(ItemCreateContext) stack}({@link ItemCreateContext#of(StackReference) ItemCreateContext.of}({@link StackReference this}).{@link ItemCreateContext.Builder#build() build()})</code>
     *
     * @return
     */
    public ItemStack referencedStack() {
        return identifier().stack(ItemCreateContext.of(this).build());
    }

    public ItemStack referencedStack(Consumer<ItemCreateContext.Builder> contextBuild) {
        ItemCreateContext.Builder builder = ItemCreateContext.of(this);
        contextBuild.accept(builder);
        return identifier().stack(builder.build());
    }

    /**
     * Gets the <b>ORIGINAL</b> stack, from which this reference was created from!<br>
     * For the linked stack from for example an external plugin use {@link #identifier()}!
     *
     * @return The <b>ORIGINAL</b> stack this reference was created from
     * @see #identifier() Get the externally linked stack of this reference!
     */
    @JsonGetter("stack")
    public ItemStack originalStack() {
        return stack;
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
        return amount() * originalStack().getAmount();
    }

    public StackIdentifierParser<?> parser() {
        return parser;
    }

    public void swapParser(StackIdentifierParser<?> parser) {
        this.parser = parser;
        parseIdentifier();
    }

    @JsonGetter("parser")
    private NamespacedKey parserId() {
        return parser.getNamespacedKey();
    }

    @Override
    public StackReference copy() {
        return new StackReference(this);
    }

    /**
     * Shrinks the specified stack by the given amount and returns the manipulated or replaced item!
     * <br><br>
     * <h3>Stackable  ({@link #originalStack()}.{@link ItemStack#getMaxStackSize() getMaxStackSize()} > 1 or stack count > 1):</h3>
     * <p>
     * The stack is shrunk by the specified amount (<strong><code>{@link #effectiveAmount()} * totalAmount</code></strong>).<br>
     * For applying stackable replacements it calls the stackReplacement function with the already shrunken stack and this reference.<br>
     * Default behaviour can be found here:
     * <ul>
     *     <li>{@link #shrink(ItemStack, int, boolean, Inventory, Player, Location)}</li>
     *     <li>{@link #shrinkUnstackableItem(ItemStack, boolean)}</li>
     * </ul>
     * </p>
     * <h3>Un-stackable  ({@link #originalStack()}.{@link ItemStack#getMaxStackSize() getMaxStackSize()} == 1 and stack count == 1):</h3>
     * <p>
     * Redirects to {@link #shrinkUnstackableItem(ItemStack, boolean)}<br>
     * </p>
     *
     * @param stack            The input ItemStack, that is also going to be edited.
     * @param count            The amount of this custom item that should be removed from the input.
     * @param useRemains       If the Item should be replaced by the default craft remains.
     * @param stackReplacement Behaviour of how to apply the replacements of stackable items.
     * @return The manipulated stack, default remain, or custom remains.
     */
    public ItemStack shrink(@NotNull ItemStack stack, int count, boolean useRemains, @NotNull BiFunction<StackIdentifier, ItemStack, ItemStack> stackReplacement) {
        return identifier().shrink(stack, count, useRemains, stackReplacement);
    }

    /**
     * Shrinks the specified stack by the given amount and returns the manipulated or replaced item!
     * <p>
     * <h3>Stackable  ({@link #originalStack()}.{@link ItemStack#getMaxStackSize() getMaxStackSize()} > 1 or stack count > 1):</h3>
     * The stack is shrunk by the specified amount (<strong><code>{@link #effectiveAmount()} * count</code></strong>)
     * <p>
     * If this stack has craft remains:<br>
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
     * <strong>Warning! If you do not provide a location via <code>player</code>, <code>inventory</code>, or <code>location</code>, then the remaining items are discarded!</strong><br>
     * For custom behaviour see {@link #shrink(ItemStack, int, boolean, BiFunction)}.
     * </p>
     * </p>
     * <p>
     * <h3>Un-stackable  ({@link #originalStack()}.{@link ItemStack#getMaxStackSize() getMaxStackSize()} == 1 and stack count == 1):</h3>
     * Redirects to {@link #shrinkUnstackableItem(ItemStack, boolean)}<br>
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
        return identifier().shrink(stack, count, useRemains, inventory, player, location);
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
        return identifier().shrinkUnstackableItem(stack, useRemains);
    }

    /**
     *
     */
    @Deprecated
    public APIReference convert() {
        return identifier().convert(weight, customAmount);
    }

    /**
     * Converts this reference to the old behaviour of the CustomItem.
     * If the reference points to a WolfyUtils CustomItem, then that item is returned.
     * Otherwise, it returns a CustomItem wrapping this reference.
     *
     * @return A CustomItem wrapping this reference, or the saved CustomItem if pointing to a WolfyUtils Item
     */
    @Deprecated
    public CustomItem convertToLegacy() {
        if (identifier() instanceof WolfyUtilsStackIdentifier wolfyUtilsStackIdentifier) {
            return wolfyUtilsStackIdentifier.customItem().orElse(new CustomItem(Material.AIR));
        }
        return new CustomItem(this);
    }

    public static class Deserializer extends StdNodeBasedDeserializer<StackReference> {

        private final WolfyUtilCore core;

        protected Deserializer() {
            super(StackReference.class);
            this.core = WolfyUtilCore.getInstance();
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
