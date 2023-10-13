package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import com.wolfyscript.utilities.Copyable;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.world.items.references.APIReference;
import com.wolfyscript.utilities.common.WolfyCore;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

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

    private StackReference(StackReference stackReference) {
        this.weight = stackReference.weight;
        this.customAmount = stackReference.customAmount;
        this.core = stackReference.core;
        this.parser = stackReference.parser;
        this.stack = stackReference.stack;
        this.identifier = parseIdentifier();
    }

    private StackIdentifier parseIdentifier() {
        return core.getRegistries().getStackIdentifierParsers().parseIdentifier(stack);
    }

    public StackIdentifier identifier() {
        return identifier;
    }

    @JsonGetter("weight")
    public double weight() {
        return weight;
    }

    @JsonGetter("amount")
    public int amount() {
        return customAmount;
    }

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

    public static class Deserializer extends StdNodeBasedDeserializer<StackReference> {

        private final WolfyCore core;

        protected Deserializer(WolfyCore core) {
            super(StackReference.class);
            this.core = core;
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
            StackIdentifier identifier = core.getRegistries().getStackIdentifierParsers().parseIdentifier(apiReference.getLinkedItem());
            return new StackReference(core, identifier.parser().getNamespacedKey(), apiReference.getWeight(), apiReference.getAmount(), apiReference.getLinkedItem());
        }
    }
}
