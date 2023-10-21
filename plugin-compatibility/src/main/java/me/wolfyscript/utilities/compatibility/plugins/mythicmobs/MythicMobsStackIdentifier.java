package me.wolfyscript.utilities.compatibility.plugins.mythicmobs;

import com.wolfyscript.utilities.bukkit.world.items.reference.ItemCreateContext;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.wolfyscript.utilities.util.NamespacedKey;
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
    public ItemStack item(ItemCreateContext context) {
        return mythicBukkit.getItemManager().getItemStack(itemName);
    }

    @Override
    public boolean matches(ItemStack other, int count, boolean exact, boolean ignoreAmount) {
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

    public static class Parser implements StackIdentifierParser<MythicMobsStackIdentifier> {

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public Optional<MythicMobsStackIdentifier> from(ItemStack itemStack) {
            var value = NBTItem.convertItemtoNBT(itemStack).getString(ITEM_KEY);
            if (value != null) {
                return Optional.of(new MythicMobsStackIdentifier(value));
            }
            return Optional.empty();
        }

        @Override
        public NamespacedKey getNamespacedKey() {
            return ID;
        }
    }

}
