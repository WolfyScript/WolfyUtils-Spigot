package me.wolfyscript.utilities.compatibility.plugins.mythicmobs;

import com.fasterxml.jackson.databind.JsonNode;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemCreateContext;
import com.wolfyscript.utilities.bukkit.world.items.reference.LegacyParser;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class MythicMobsStackIdentifier implements StackIdentifier {

    protected static final String ITEM_KEY = "MYTHIC_TYPE";
    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("mythicmobs");

    private final MythicBukkit mythicBukkit;
    private final String itemName;

    public MythicMobsStackIdentifier(String itemName) {
        this.itemName = itemName;
        this.mythicBukkit = MythicBukkit.inst();
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        ItemStack stack = mythicBukkit.getItemManager().getItemStack(itemName);
        if (stack != null) {
            stack.setAmount(context.amount());
        }
        return stack;
    }

    @Override
    public boolean matches(ItemStack other, int count, boolean exact, boolean ignoreAmount) {
        if (ItemUtils.isAirOrNull(other)) return false;
        var value = NBTItem.convertItemtoNBT(other).getString(ITEM_KEY);
        if (value != null) {
            return Objects.equals(this.itemName, value);
        }
        return false;
    }

    @Override
    public MythicMobs5RefImpl convert(double weight, int amount) {
        MythicMobs5RefImpl ref = new MythicMobs5RefImpl(itemName);
        ref.setWeight(weight);
        ref.setAmount(amount);
        return ref;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return ID;
    }

    public static class Parser implements StackIdentifierParser<MythicMobsStackIdentifier>, LegacyParser<MythicMobsStackIdentifier> {

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public Optional<MythicMobsStackIdentifier> from(ItemStack itemStack) {
            if (ItemUtils.isAirOrNull(itemStack)) return Optional.empty();
            var tag = NBTItem.convertItemtoNBT(itemStack).getCompound("tag");
            if (tag == null) return Optional.empty();
            var value = tag.getString(ITEM_KEY);
            if (value != null) {
                return Optional.of(new MythicMobsStackIdentifier(value));
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
                    Component.text("MythicMobs").color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD),
                    new DisplayConfiguration.MaterialIconSettings(Material.WITHER_SKELETON_SKULL)
            );
        }

        @Override
        public Optional<MythicMobsStackIdentifier> from(JsonNode legacyData) {
            return Optional.of(new MythicMobsStackIdentifier(legacyData.asText()));
        }
    }

}
