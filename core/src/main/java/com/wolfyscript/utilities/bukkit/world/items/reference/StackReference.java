package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import com.google.common.collect.Streams;
import com.wolfyscript.utilities.Copyable;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.custom_items.references.VanillaRef;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.json.jackson.serialization.APIReferenceSerialization;
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
import java.util.function.Function;

/**
 * Acts as a wrapper for {@link StackIdentifier}, that links to an external ItemStack (like other Plugins).
 * This keeps track of the original ItemStack, as a fallback, and the parser used to get the wrapped {@link StackIdentifier}.
 * Additionally, it stores the amount, and other extra settings.
 */
@JsonDeserialize(using = StackReference.Deserializer.class)
public class StackReference implements Copyable<StackReference> {

    private final WolfyUtilCore core;
    private final int amount;
    private final double weight;
    /**
     * Used to store the original stack
     */
    @JsonIgnore
    protected ItemStack stack;
    /**
     * Used to store the previous parser result
     */
    protected StackIdentifier identifier;

    private NamespacedKey parserKey;
    private StackIdentifierParser<?> parser;

    public static StackReference of(ItemStack itemStack) {
        return new StackReference(WolfyUtilCore.getInstance(), itemStack.getAmount(), 1, new BukkitStackIdentifier(itemStack), itemStack);
    }

    /**
     * @deprecated use {@link StackReference#StackReference(WolfyUtilCore, int, double, StackIdentifier, ItemStack)} instead!
     */
    @Deprecated
    public StackReference(@NotNull WolfyUtilCore core, @NotNull StackIdentifier identifier, double weight, int amount, @Nullable ItemStack itemStack) {
        this(core, amount, weight, identifier, itemStack);
    }

    @Deprecated
    public StackReference(@NotNull WolfyUtilCore core, @NotNull NamespacedKey parserKey, int amount, double weight, @Nullable ItemStack item) {
        this(core, amount, weight, parserKey, item);
    }

    public StackReference(@NotNull WolfyUtilCore core, int amount, double weight, @NotNull StackIdentifier identifier, @Nullable ItemStack itemStack) {
        this.core = core;
        this.amount = amount;
        this.weight = weight;
        this.identifier = identifier;
        this.parser = identifier.parser();
        this.parserKey = parser.getNamespacedKey();
        this.stack = itemStack;
    }

    public StackReference(@NotNull WolfyUtilCore core, int amount, double weight, @NotNull NamespacedKey parserKey, @Nullable ItemStack item) {
        this.amount = amount;
        this.weight = weight;
        this.core = core;
        this.parserKey = parserKey;
        this.stack = item; // Set item before parsing it
        this.identifier = getOrParseIdentifier();
    }

    private StackReference(StackReference stackReference) {
        this.weight = stackReference.weight;
        this.amount = stackReference.amount;
        this.core = stackReference.core;
        this.parserKey = stackReference.parserKey;
        this.stack = stackReference.stack;
        this.identifier = getOrParseIdentifier();
    }

    /**
     * Swaps the current parser with the specified parser and parses the original stack to get the new StackIdentifier.
     *
     * @param parser The new parser to use to get the StackIdentifier
     */
    public void swapParser(StackIdentifierParser<?> parser) {
        this.parserKey = parser.getNamespacedKey();
        this.identifier = null; // Reset cached identifier, so it gets parsed again
        this.identifier = getOrParseIdentifier();
    }

    /**
     * Parses the identifier when it is not available, or returns the current identifier.
     *
     * @return The parsed Identifier, or null if not available.
     */
    @JsonGetter("identifier")
    protected StackIdentifier getOrParseIdentifier() {
        if (identifier == null) {
            if (parser() == null) return null;
            identifier = parser.from(stack).orElse(null);
        }
        return identifier;
    }

    /**
     * Gets the currently used {@link StackIdentifierParser}
     *
     * @return The current {@link StackIdentifierParser}
     */
    public StackIdentifierParser<?> parser() {
        if (parser == null || !parser.getNamespacedKey().equals(parserKey)) {
            parser = core.getRegistries().getStackIdentifierParsers().get(parserKey);
        }
        return parser; // Parser is still cached and wasn't changed
    }

    /**
     * Gets the currently wrapped StackIdentifier, parsed by the current {@link StackIdentifierParser}
     *
     * @return The currently wrapped StackIdentifier
     */
    public Optional<StackIdentifier> identifier() {
        return Optional.ofNullable(getOrParseIdentifier());
    }

    public boolean matches(ItemStack other) {
        return matches(other, true, false);
    }

    public boolean matches(ItemStack other, boolean exact) {
        return matches(other, exact, false);
    }

    public boolean matches(ItemStack other, boolean exact, boolean ignoreAmount) {
        if (ItemUtils.isAirOrNull(other)) return false;
        if (!ignoreAmount && other.getAmount() < amount) return false;
        return identifier().map(identifier -> identifier.matchesIgnoreCount(other, exact)).orElse(false);
    }

    /**
     * Convenience method to get the stack the identifier points to.<br>
     * This is the same as <code>{@link #identifier() identifier()}.{@link StackIdentifier#stack(ItemCreateContext) stack}({@link ItemCreateContext#of(StackReference) ItemCreateContext.of}({@link StackReference this}).{@link ItemCreateContext.Builder#build() build()})</code>
     *
     * @return The stack the {@link #identifier()} points to
     */
    public ItemStack referencedStack() {
        return identifier().map(stackIdentifier -> stackIdentifier.stack(ItemCreateContext.of(this).build())).orElse(new ItemStack(Material.AIR));
    }

    /**
     * Convenience method to get the stack the identifier points to.<br>
     * This is the same as <code>{@link #identifier() identifier()}.{@link StackIdentifier#stack(ItemCreateContext) stack}({@link ItemCreateContext#of(StackReference) ItemCreateContext.of}({@link StackReference this}).{@link ItemCreateContext.Builder#build() build()})</code>
     *
     * @param contextBuild provides a {@link ItemCreateContext.Builder} with this reference already applied
     * @return The stack the {@link #identifier()} points to
     */
    public ItemStack referencedStack(Consumer<ItemCreateContext.Builder> contextBuild) {
        return identifier().map(stackIdentifier -> {
            ItemCreateContext.Builder builder = ItemCreateContext.of(this);
            contextBuild.accept(builder);
            return stackIdentifier.stack(builder.build());
        }).orElse(new ItemStack(Material.AIR));
    }

    /**
     * Gets the <b>ORIGINAL</b> stack, from which this reference was created from!<br>
     * For the linked stack from for example an external plugin use {@link #identifier()}!
     *
     * @return The <b>ORIGINAL</b> stack this reference was created from
     * @see #identifier() Get the StackIdentifier pointing to the external stack
     * @see #referencedStack() Get the externally referenced ItemStack
     */
    public ItemStack originalStack() {
        return stack;
    }

    /**
     * Gets the weight associated with this reference inside a collection.<br>
     * For example inside of a {@link me.wolfyscript.utilities.util.RandomCollection<StackReference>}
     *
     * @return The weight of this reference
     */
    @JsonGetter("weight")
    public double weight() {
        return weight;
    }

    /**
     * Gets the stack amount for the referenced ItemStack
     *
     * @return The stack amount of the referenced ItemStack
     */
    @JsonGetter("amount")
    public int amount() {
        return amount;
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
     * The stack is shrunk by the specified amount (<strong><code>{@link #amount()} * totalAmount</code></strong>).<br>
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
        return identifier().map(stackIdentifier -> stackIdentifier.shrink(stack, count * amount(), useRemains, stackReplacement)).orElse(stack);
    }

    /**
     * Shrinks the specified stack by the given amount and returns the manipulated or replaced item!
     * <p>
     * <h3>Stackable  ({@link #originalStack()}.{@link ItemStack#getMaxStackSize() getMaxStackSize()} > 1 or stack count > 1):</h3>
     * The stack is shrunk by the specified amount (<strong><code>{@link #amount()} * count</code></strong>)
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
        return identifier().map(stackIdentifier -> stackIdentifier.shrink(stack, count * amount(), useRemains, inventory, player, location)).orElse(stack);
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
        return identifier().map(stackIdentifier -> stackIdentifier.shrinkUnstackableItem(stack, useRemains)).orElse(stack);
    }


    public ItemStack shrinkUnstackableItem(ItemStack stack, boolean useRemains, BiFunction<StackIdentifier, ItemStack, Optional<ItemStack>> remainsFunction, Function<ItemStack, ItemStack> manipulator) {
        return identifier().map(stackIdentifier -> stackIdentifier.shrinkUnstackableItem(stack, useRemains, remainsFunction, manipulator)).orElse(stack);
    }

    /**
     * Converts this StackReference into a legacy APIReference.
     */
    @Deprecated
    public APIReference convert() {
        return identifier().map(stackIdentifier -> stackIdentifier.convert(weight, amount)).orElse(new VanillaRef(new ItemStack(Material.AIR)));
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
        return identifier().map(stackIdentifier -> {
            if (stackIdentifier instanceof WolfyUtilsStackIdentifier wolfyUtilsStackIdentifier) {
                return wolfyUtilsStackIdentifier.customItem().orElse(new CustomItem(Material.AIR));
            }
            return new CustomItem(Material.AIR);
        }).orElse(new CustomItem(Material.AIR));
    }

    public static class Deserializer extends StdNodeBasedDeserializer<StackReference> {

        private final WolfyUtilCore core;

        protected Deserializer() {
            super(StackReference.class);
            this.core = WolfyUtilCore.getInstance();
        }

        @Override
        public StackReference convert(JsonNode root, DeserializationContext ctxt) throws IOException {
            var hasIdentifier = root.has("identifier");
            if (hasIdentifier || root.has("parser")) {
                double weight = root.get("weight").asDouble(1);
                weight = weight <= 0 ? 1 : weight; // make sure weight is greater than 0, so it never disappears unintentionally (e.g. in Recipe results)!
                int amount = root.get("amount").asInt(1);

                // New stack references format saves the identifier directly
                // But can still optionally contain the original stack
                ItemStack originalStack = null;
                if (root.has("stack")) {
                    originalStack = ctxt.readTreeAsValue(root.get("stack"), ItemStack.class);
                }

                if (hasIdentifier) {
                    // Identifier is defined, use defined identifier
                    var identifier = ctxt.readTreeAsValue(root.get("identifier"), StackIdentifier.class);
                    if (originalStack == null) {
                        originalStack = identifier.stack(ItemCreateContext.empty(1));
                    }
                    return new StackReference(core, amount, weight, identifier, originalStack);
                }

                // Parser is defined, parse form original stack
                var parserKey = ctxt.readTreeAsValue(root.get("parser"), NamespacedKey.class);
                return new StackReference(core, amount, weight, parserKey, originalStack);
            }

            // Legacy API Reference! Need to convert!
            if (root.isObject()) {
                int customAmount = root.path(APIReferenceSerialization.CUSTOM_AMOUNT).asInt(0);
                double weight = root.path(APIReferenceSerialization.WEIGHT).asDouble(1);
                return Streams.stream(root.fieldNames()).filter(s -> !s.equals(APIReferenceSerialization.WEIGHT) && !s.equals(APIReferenceSerialization.CUSTOM_AMOUNT)).findFirst()
                        .map(key -> {
                            APIReference.Parser<?> parser = CustomItem.getApiReferenceParser(key);
                            if (parser != null) {
                                APIReference reference = parser.parse(root.path(key));
                                if (reference != null) {
                                    reference.setAmount(customAmount);
                                    reference.setWeight(weight);
                                    return reference.convertToStackReference();
                                }
                            }
                            return new LegacyStackReference(core, NamespacedKey.wolfyutilties(key), weight, customAmount, root.path(key));
                        }).orElseGet(() -> StackReference.of(ItemUtils.AIR));
            }
            if (root.isTextual()) {
                //Legacy items saved as string!
                APIReference apiReference = ctxt.readTreeAsValue(root, VanillaRef.class);
                if (apiReference != null) {
                    return StackReference.of(apiReference.getLinkedItem() != null ? apiReference.getLinkedItem() : ItemUtils.AIR);
                }
                return StackReference.of(ItemUtils.AIR);
            }
            // Unknown type
            return StackReference.of(ItemUtils.AIR);
        }
    }
}
