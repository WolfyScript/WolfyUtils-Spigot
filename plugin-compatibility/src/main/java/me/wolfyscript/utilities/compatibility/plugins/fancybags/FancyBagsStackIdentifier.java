package me.wolfyscript.utilities.compatibility.plugins.fancybags;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemCreateContext;
import com.wolfyscript.utilities.bukkit.world.items.reference.LegacyParser;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTType;
import me.chickenstyle.backpack.Backpack;
import me.chickenstyle.backpack.Utils;
import me.chickenstyle.backpack.configs.CustomBackpacks;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@KeyedStaticId(key = "fancybags")
public class FancyBagsStackIdentifier implements StackIdentifier {

    private static final String ID_TAG = "BackpackID";
    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("fancybags");

    private final int id;

    @JsonCreator
    public FancyBagsStackIdentifier(@JsonProperty("id") int id) {
        this.id = id;
    }

    @JsonGetter("id")
    public int id() {
        return id;
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        Backpack bag = CustomBackpacks.getBackpack(id);
        if (bag != null) {
            ItemStack stack = Utils.createBackpackItemStack(bag.getName(), bag.getTexture(), bag.getSlotsAmount(), bag.getId());
            stack.setAmount(context.amount());
            return stack;
        }
        return new ItemStack(Material.AIR);
    }

    @Override
    public boolean matchesIgnoreCount(ItemStack other, boolean exact) {
        NBTItem nbtItem = new NBTItem(other);
        if (nbtItem.hasTag(ID_TAG, NBTType.NBTTagInt)) {
            return nbtItem.getInteger(ID_TAG) == id;
        }
        return false;
    }

    @Override
    public FancyBagsItemsRef convert(double weight, int amount) {
        FancyBagsItemsRef ref = new FancyBagsItemsRef(id);
        ref.setWeight(weight);
        ref.setAmount(amount);
        return ref;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return ID;
    }

    public static class Parser implements StackIdentifierParser<FancyBagsStackIdentifier>, LegacyParser<FancyBagsStackIdentifier> {

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public Optional<FancyBagsStackIdentifier> from(ItemStack itemStack) {
            if (ItemUtils.isAirOrNull(itemStack)) return Optional.empty();
            NBTItem nbtItem = new NBTItem(itemStack);
            if (nbtItem.hasTag(ID_TAG, NBTType.NBTTagInt)) {
                return Optional.of(new FancyBagsStackIdentifier(nbtItem.getInteger(ID_TAG)));
            }
            return Optional.empty();
        }

        @Override
        public NamespacedKey getNamespacedKey() {
            return ID;
        }

        @Override
        public Optional<FancyBagsStackIdentifier> from(JsonNode legacyData) {
            return Optional.of(new FancyBagsStackIdentifier(legacyData.asInt()));
        }
    }

}
