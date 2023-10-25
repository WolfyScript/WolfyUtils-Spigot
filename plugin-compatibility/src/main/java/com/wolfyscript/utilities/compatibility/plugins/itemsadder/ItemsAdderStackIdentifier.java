package me.wolfyscript.utilities.compatibility.plugins.itemsadder;

import com.wolfyscript.utilities.bukkit.world.items.reference.ItemCreateContext;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import dev.lone.itemsadder.api.CustomStack;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class ItemsAdderStackIdentifier implements StackIdentifier {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("itemsadder");

    private final String itemId;

    public ItemsAdderStackIdentifier(String itemId) {
        this.itemId = itemId;
    }

    public String itemId() {
        return itemId;
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        var customStack = CustomStack.getInstance(itemId);
        if (customStack != null) {
            return customStack.getItemStack();
        }
        return ItemUtils.AIR;
    }

    @Override
    public boolean matches(ItemStack other, int count, boolean exact, boolean ignoreAmount) {
        if (!ignoreAmount && other.getAmount() < stack(ItemCreateContext.empty(count)).getAmount() * count) return false;
        var customStack = CustomStack.byItemStack(other);
        return customStack != null && Objects.equals(itemId, customStack.getNamespacedID());
    }

    @Override
    public ItemsAdderRefImpl convert(double weight, int amount) {
        ItemsAdderRefImpl ref = new ItemsAdderRefImpl(itemId);
        ref.setWeight(weight);
        ref.setAmount(amount);
        return ref;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return ID;
    }

    public static class Parser implements StackIdentifierParser<ItemsAdderStackIdentifier> {

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public Optional<ItemsAdderStackIdentifier> from(ItemStack itemStack) {
            var customStack = CustomStack.byItemStack(itemStack);
            if (customStack != null) {
                return Optional.of(new ItemsAdderStackIdentifier(customStack.getNamespacedID()));
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
                    Component.text("ItemsAdder").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD),
                    new DisplayConfiguration.MaterialIconSettings(Material.GRASS_BLOCK)
            );
        }
    }

}
