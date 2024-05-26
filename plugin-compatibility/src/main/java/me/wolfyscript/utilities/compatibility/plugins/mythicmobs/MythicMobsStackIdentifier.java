package me.wolfyscript.utilities.compatibility.plugins.mythicmobs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.wolfyscript.utilities.compatibility.plugins.MythicMobsIntegration;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

@KeyedStaticId(key = "mythicmobs")
@DependencyResolverSettings(PluginIntegrationDependencyResolver.class)
@PluginIntegrationDependencyResolverSettings(pluginName = MythicMobsIntegration.KEY, integration = MythicMobsIntegration.class)
public class MythicMobsStackIdentifier implements StackIdentifier {

    protected static final String ITEM_KEY = "MYTHIC_TYPE";
    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("mythicmobs");

    @JsonIgnore
    private final MythicBukkit mythicBukkit;
    private final String itemName;

    @JsonCreator
    public MythicMobsStackIdentifier(@JsonProperty("item") String itemName) {
        this.itemName = itemName;
        this.mythicBukkit = MythicBukkit.inst();
    }

    @JsonGetter("item")
    public String getItemName() {
        return itemName;
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
    public boolean matchesIgnoreCount(ItemStack other, boolean exact) {
        if (ItemUtils.isAirOrNull(other)) return false;
        var tag = NBTItem.convertItemtoNBT(other).getCompound("tag");
        if(tag == null) return false;
        if (tag.hasTag(ITEM_KEY)) {
            return Objects.equals(this.itemName, tag.getString(ITEM_KEY));
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
            return 1600;
        }

        @Override
        public Optional<MythicMobsStackIdentifier> from(ItemStack itemStack) {
            if (ItemUtils.isAirOrNull(itemStack)) return Optional.empty();
            var tag = NBTItem.convertItemtoNBT(itemStack).getCompound("tag");
            if (tag == null) return Optional.empty();
            if (tag.hasTag(ITEM_KEY)) {
                return Optional.of(new MythicMobsStackIdentifier(tag.getString(ITEM_KEY)));
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
