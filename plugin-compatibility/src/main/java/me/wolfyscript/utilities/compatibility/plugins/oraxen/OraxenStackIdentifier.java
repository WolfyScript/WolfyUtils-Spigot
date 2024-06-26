package me.wolfyscript.utilities.compatibility.plugins.oraxen;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.dependency.PluginIntegrationDependencyResolver;
import com.wolfyscript.utilities.bukkit.dependency.PluginIntegrationDependencyResolverSettings;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemCreateContext;
import com.wolfyscript.utilities.bukkit.world.items.reference.LegacyParser;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import com.wolfyscript.utilities.dependency.DependencyResolverSettings;
import io.th0rgal.oraxen.api.OraxenItems;
import me.wolfyscript.utilities.compatibility.plugins.OraxenIntegration;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

@KeyedStaticId(key = "oraxen")
@DependencyResolverSettings(PluginIntegrationDependencyResolver.class)
@PluginIntegrationDependencyResolverSettings(pluginName = OraxenIntegration.KEY, integration = OraxenIntegration.class)
public class OraxenStackIdentifier implements StackIdentifier {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("oraxen");

    private final String itemID;

    @JsonCreator
    public OraxenStackIdentifier(@JsonProperty("item") String itemID) {
        this.itemID = itemID;
    }

    @JsonGetter("item")
    public String itemId() {
        return itemID;
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        if (OraxenItems.exists(itemID)) {
            ItemStack stack = OraxenItems.getItemById(itemID).build();
            stack.setAmount(context.amount());
            return stack;
        }
        return ItemUtils.AIR;
    }

    @Override
    public boolean matchesIgnoreCount(ItemStack other, boolean exact) {
        String itemId = OraxenItems.getIdByItem(other);
        if (itemId != null && !itemId.isEmpty()) {
            return Objects.equals(this.itemID, itemId);
        }
        return false;
    }

    @Override
    public OraxenRefImpl convert(double weight, int amount) {
        OraxenRefImpl ref = new OraxenRefImpl(itemID);
        ref.setWeight(weight);
        ref.setAmount(amount);
        return ref;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return ID;
    }

    public static class Parser implements StackIdentifierParser<OraxenStackIdentifier>, LegacyParser<OraxenStackIdentifier> {

        @Override
        public int priority() {
            return 1900;
        }

        @Override
        public Optional<OraxenStackIdentifier> from(ItemStack itemStack) {
            String itemId = OraxenItems.getIdByItem(itemStack);
            if (itemId != null && !itemId.isEmpty()) {
                return Optional.of(new OraxenStackIdentifier(itemId));
            }
            return Optional.empty();
        }

        @Override
        public NamespacedKey getNamespacedKey() {
            return ID;
        }

        @Override
        public DisplayConfiguration displayConfig() {
            return new DisplayConfiguration.SimpleDisplayConfig(
                    Component.text("Oraxen").color(NamedTextColor.DARK_AQUA).decorate(TextDecoration.BOLD),
                    new DisplayConfiguration.MaterialIconSettings(Material.DIAMOND)
            );
        }

        @Override
        public Optional<OraxenStackIdentifier> from(JsonNode legacyData) {
            return Optional.of(new OraxenStackIdentifier(legacyData.asText()));
        }
    }

}
