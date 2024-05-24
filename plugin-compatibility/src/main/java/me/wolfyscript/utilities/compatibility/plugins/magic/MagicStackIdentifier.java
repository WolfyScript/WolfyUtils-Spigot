package me.wolfyscript.utilities.compatibility.plugins.magic;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.dependency.PluginIntegrationDependencyResolver;
import com.wolfyscript.utilities.bukkit.dependency.PluginIntegrationDependencyResolverSettings;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemCreateContext;
import com.wolfyscript.utilities.bukkit.world.items.reference.LegacyParser;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import com.wolfyscript.utilities.dependency.DependencyResolverSettings;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

@KeyedStaticId(key = "magic")
@DependencyResolverSettings(PluginIntegrationDependencyResolver.class)
@PluginIntegrationDependencyResolverSettings(pluginName = MagicImpl.PLUGIN_NAME, integration = MagicImpl.class)
public class MagicStackIdentifier implements StackIdentifier {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("magic");

    private final String itemKey;
    @JsonIgnore
    private final MagicAPI magicAPI;

    @JsonCreator
    public MagicStackIdentifier(@JsonProperty("itemKey") String itemKey) {
        this(Bukkit.getPluginManager().getPlugin("Magic") instanceof MagicAPI api ? api : null, itemKey);
    }

    public MagicStackIdentifier(MagicAPI magicAPI, String itemKey) {
        Preconditions.checkNotNull(magicAPI, "No MagicAPI specified when creating a MagicStackIdentifier");
        this.magicAPI = magicAPI;
        this.itemKey = itemKey;
    }

    @JsonGetter("itemKey")
    public String getItemKey() {
        return itemKey;
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        ItemStack stack = Objects.requireNonNullElse(magicAPI.getController().createItem(itemKey), ItemUtils.AIR);
        stack.setAmount(context.amount());
        return stack;
    }

    @Override
    public boolean matchesIgnoreCount(ItemStack other, boolean exact) {
        return Objects.equals(magicAPI.getController().getItemKey(other), itemKey);
    }

    @Override
    public MagicRefImpl convert(double weight, int amount) {
        MagicRefImpl ref = new MagicRefImpl(itemKey);
        ref.setWeight(weight);
        ref.setAmount(amount);
        return ref;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return ID;
    }

    public static class Parser implements StackIdentifierParser<MagicStackIdentifier>, LegacyParser<MagicStackIdentifier> {

        private final MagicAPI magicAPI;

        public Parser(MagicAPI magicAPI) {
            this.magicAPI = magicAPI;
        }

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public Optional<MagicStackIdentifier> from(ItemStack itemStack) {
            if(magicAPI.isBrush(itemStack) || magicAPI.isSpell(itemStack) || magicAPI.isUpgrade(itemStack) || magicAPI.isWand(itemStack)) {
                return Optional.of(new MagicStackIdentifier(magicAPI.getItemKey(itemStack)));
            }
            return Optional.empty();
        }

        @Override
        public NamespacedKey getNamespacedKey() {
            return ID;
        }

        @Override
        public Optional<MagicStackIdentifier> from(JsonNode legacyData) {
            return Optional.of(new MagicStackIdentifier(legacyData.asText()));
        }
    }

}
