package me.wolfyscript.utilities.compatibility.plugins.oraxen;

import com.fasterxml.jackson.databind.JsonNode;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemCreateContext;
import com.wolfyscript.utilities.bukkit.world.items.reference.LegacyParser;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import io.th0rgal.oraxen.api.OraxenItems;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class OraxenStackIdentifier implements StackIdentifier {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("oraxen");

    private final String itemID;

    public OraxenStackIdentifier(String itemID) {
        this.itemID = itemID;
    }

    public String itemId() {
        return itemID;
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        if (OraxenItems.exists(itemID)) {
            return OraxenItems.getItemById(itemID).setAmount(context.amount()).regen().build();
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
            return 0;
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
