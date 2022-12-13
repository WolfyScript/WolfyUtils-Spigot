package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;

// At this moment of time, this reference does not save the complete ItemStack, therefor don't use it (Set priority lower than BukkitItemReference)!
@ItemReferenceParserSettings(priority = -1000, parser = SimpleBukkitItemReference.Parser.class)
@KeyedStaticId(key = "simple")
public class SimpleBukkitItemReference extends ItemReference {

    @JsonIgnore
    private ItemStack cachedStack;

    private final BukkitItemStackConfig config;
    @JsonCreator
    public SimpleBukkitItemReference(@JacksonInject WolfyUtils wolfyUtils, @JsonProperty("config") BukkitItemStackConfig config) {
        super(wolfyUtils);
        this.config = config;
    }

    protected SimpleBukkitItemReference(SimpleBukkitItemReference reference) {
        super(reference);
        this.cachedStack = null;
        this.config = reference.config;
    }

    @Override
    public SimpleBukkitItemReference copy() {
        return new SimpleBukkitItemReference(this);
    }

    @JsonIgnore
    @Override
    public ItemStack getItem() {
        if (cachedStack == null) {
            cachedStack = config.constructItemStack();
        }
        return cachedStack;
    }

    @Override
    public boolean isValidItem(ItemStack other) {
        if (cachedStack == null) {
            cachedStack = config.constructItemStack();
        }
        return cachedStack.isSimilar(other);
    }

    public BukkitItemStackConfig getConfig() {
        return config;
    }

    @Override
    public String toString() {
        return "SimpleBukkitItemReference{" +
                "cachedStack=" + cachedStack +
                ", config=" + config +
                "} " + super.toString();
    }

    public static class Parser implements ItemReference.Parser<SimpleBukkitItemReference> {

        @Override
        public Optional<SimpleBukkitItemReference> parseFromStack(WolfyUtils wolfyUtils, ItemStack stack) {
            return Optional.of(new SimpleBukkitItemReference(wolfyUtils, new BukkitItemStackConfig(wolfyUtils, stack)));
        }
    }

}
