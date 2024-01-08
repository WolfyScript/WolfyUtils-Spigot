package me.wolfyscript.utilities.compatibility.plugins.executableitems;

import com.fasterxml.jackson.databind.JsonNode;
import com.ssomar.score.api.executableitems.config.ExecutableItemsManagerInterface;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemCreateContext;
import com.wolfyscript.utilities.bukkit.world.items.reference.LegacyParser;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ExecutableItemsStackIdentifier implements StackIdentifier {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("executableitems");

    private final ExecutableItemsManagerInterface manager;
    private final String id;

    public ExecutableItemsStackIdentifier(ExecutableItemsManagerInterface manager, String id) {
        this.id = id;
        this.manager = manager;
    }

    private ExecutableItemsStackIdentifier(ExecutableItemsStackIdentifier other) {
        this.id = other.id;
        this.manager = other.manager;
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        return manager.getExecutableItem(id).map(item -> item.buildItem(context.amount(), context.player())).orElseGet(()-> new ItemStack(Material.AIR));
    }

    @Override
    public boolean matches(ItemStack other, int count, boolean exact, boolean ignoreAmount) {
        if (ItemUtils.isAirOrNull(other)) return false;
        return manager.getExecutableItem(other).map(exeItem -> exeItem.getId().equals(id)).orElse(false);
    }

    @Override
    public ExecutableItemsRef convert(double weight, int amount) {
        ExecutableItemsRef ref = new ExecutableItemsRef(manager, id);
        ref.setWeight(weight);
        ref.setAmount(amount);
        return ref;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return ID;
    }

    public static class Parser implements StackIdentifierParser<ExecutableItemsStackIdentifier>, LegacyParser<ExecutableItemsStackIdentifier> {

        private final ExecutableItemsManagerInterface manager;

        public Parser(ExecutableItemsManagerInterface manager) {
            this.manager = manager;
        }

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public Optional<ExecutableItemsStackIdentifier> from(ItemStack itemStack) {
            return manager.getExecutableItem(itemStack).map(exeItem -> new ExecutableItemsStackIdentifier(manager, exeItem.getId()));
        }

        @Override
        public NamespacedKey getNamespacedKey() {
            return ID;
        }

        @Override
        public Optional<ExecutableItemsStackIdentifier> from(JsonNode legacyData) {
            return Optional.of(new ExecutableItemsStackIdentifier(manager, legacyData.asText()));
        }
    }

}
