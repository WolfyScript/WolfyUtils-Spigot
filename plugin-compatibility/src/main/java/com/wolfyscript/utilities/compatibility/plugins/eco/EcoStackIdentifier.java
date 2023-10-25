package me.wolfyscript.utilities.compatibility.plugins.eco;

import com.willfp.eco.core.items.Items;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemCreateContext;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import me.wolfyscript.utilities.util.NamespacedKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class EcoStackIdentifier implements StackIdentifier {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("eco");

    private final org.bukkit.NamespacedKey itemKey;

    public EcoStackIdentifier(org.bukkit.NamespacedKey itemKey) {
        this.itemKey = itemKey;
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        return Items.lookup(itemKey.toString()).getItem();
    }

    @Override
    public boolean matches(ItemStack other, int count, boolean exact, boolean ignoreAmount) {
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

    public static class Parser implements StackIdentifierParser<EcoStackIdentifier> {

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
    }

}
