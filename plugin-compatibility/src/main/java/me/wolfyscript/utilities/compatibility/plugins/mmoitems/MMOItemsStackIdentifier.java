package me.wolfyscript.utilities.compatibility.plugins.mmoitems;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemCreateContext;
import com.wolfyscript.utilities.bukkit.world.items.reference.LegacyParser;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import io.lumine.mythic.lib.api.item.NBTItem;
import me.wolfyscript.utilities.compatibility.plugins.mythicmobs.MythicMobsStackIdentifier;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

@KeyedStaticId(key = "mmoitems")
public class MMOItemsStackIdentifier implements StackIdentifier {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("mmoitems");

    private final Type itemType;
    private final String itemName;

    public MMOItemsStackIdentifier(@JsonProperty("type") Type itemType, @JsonProperty("name") String itemName) {
        this.itemType = itemType;
        this.itemName = itemName;
    }

    @JsonCreator
    MMOItemsStackIdentifier(@JsonProperty("type") String itemType, @JsonProperty("name") String itemName) {
        this.itemType = MMOItems.plugin.getTypes().get(itemType);
        this.itemName = itemName;
    }

    @JsonGetter("name")
    public String getItemName() {
        return itemName;
    }

    @JsonGetter("type")
    public Type getItemType() {
        return itemType;
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        MMOItem item = MMOItems.plugin.getMMOItem(itemType, itemName);
        if (item == null) return null;
        ItemStack stack = item.newBuilder().buildSilently();
        stack.setAmount(context.amount());
        return stack;
    }

    @Override
    public boolean matchesIgnoreCount(ItemStack other, boolean exact) {
        if (ItemUtils.isAirOrNull(other)) return false;
        var nbtItem = NBTItem.get(other);
        if (!nbtItem.hasType()) return false;
        return Objects.equals(this.itemType, MMOItems.plugin.getTypes().get(nbtItem.getType())) && Objects.equals(this.itemName, nbtItem.getString("MMOITEMS_ITEM_ID"));
    }

    @Override
    public MMOItemsRefImpl convert(double weight, int amount) {
        MMOItemsRefImpl ref = new MMOItemsRefImpl(itemType, itemName);
        ref.setWeight(weight);
        ref.setAmount(amount);
        return ref;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return ID;
    }

    public static class Parser implements StackIdentifierParser<MMOItemsStackIdentifier>, LegacyParser<MMOItemsStackIdentifier> {

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public Optional<MMOItemsStackIdentifier> from(ItemStack itemStack) {
            if (ItemUtils.isAirOrNull(itemStack)) return Optional.empty();
            NBTItem nbtItem = NBTItem.get(itemStack);
            if (nbtItem.hasType()) {
                Type type = MMOItems.plugin.getTypes().get(nbtItem.getType());
                String itemId = nbtItem.getString("MMOITEMS_ITEM_ID");
                return Optional.of(new MMOItemsStackIdentifier(type, itemId));
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
                    Component.text("MMOItems").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.BOLD),
                    new DisplayConfiguration.MaterialIconSettings(Material.IRON_SWORD)
            );
        }

        @Override
        public Optional<MMOItemsStackIdentifier> from(JsonNode legacyData) {
            if (legacyData.has("type") && legacyData.has("name")) {
                String typeID = legacyData.get("type").asText();
                if (MMOItems.plugin.getTypes().has(typeID)) {
                    return Optional.of(new MMOItemsStackIdentifier(MMOItems.plugin.getTypes().get(typeID), legacyData.get("name").asText()));
                }
            }
            return Optional.empty();
        }
    }

}
