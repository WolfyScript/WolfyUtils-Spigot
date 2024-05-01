package me.wolfyscript.utilities.compatibility.plugins.eco;

import com.fasterxml.jackson.databind.JsonNode;
import com.willfp.eco.core.items.Items;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemCreateContext;
import com.wolfyscript.utilities.bukkit.world.items.reference.LegacyParser;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

@KeyedStaticId(key = "eco")
public class EcoStackIdentifier implements StackIdentifier {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("eco");

    private final org.bukkit.NamespacedKey itemKey;

    public EcoStackIdentifier(org.bukkit.NamespacedKey itemKey) {
        this.itemKey = itemKey;
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        ItemStack stack = Items.lookup(itemKey.toString()).getItem();
        if (stack != null) {
            stack.setAmount(context.amount());
        }
        return stack;
    }

    @Override
    public boolean matchesIgnoreCount(ItemStack other, boolean exact) {
        if (ItemUtils.isAirOrNull(other)) return false;
        var item = Items.getCustomItem(other);
        return item != null && Objects.equals(itemKey, item.getKey());
    }

    @Override
    public EcoRefImpl convert(double weight, int amount) {
        EcoRefImpl ref = new EcoRefImpl(itemKey);
        ref.setWeight(weight);
        ref.setAmount(amount);
        return ref;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return ID;
    }

    public static class Parser implements StackIdentifierParser<EcoStackIdentifier>, LegacyParser<EcoStackIdentifier> {

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public Optional<EcoStackIdentifier> from(ItemStack itemStack) {
            if (Items.isCustomItem(itemStack)) {
                var customStack = Items.getCustomItem(itemStack);
                if (customStack != null) {
                    return Optional.of(new EcoStackIdentifier(customStack.getKey()));
                }
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
                    Component.text("Eco").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD),
                    new DisplayConfiguration.MaterialIconSettings(Material.EMERALD)
            );
        }

        @Override
        public Optional<EcoStackIdentifier> from(JsonNode legacyData) {
            return Optional.of(new EcoStackIdentifier(org.bukkit.NamespacedKey.fromString(legacyData.asText())));
        }
    }

}
