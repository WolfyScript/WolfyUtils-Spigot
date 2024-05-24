package me.wolfyscript.utilities.compatibility.plugins.executableblocks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.ssomar.executableblocks.executableblocks.ExecutableBlocksManager;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.dependency.PluginIntegrationDependencyResolver;
import com.wolfyscript.utilities.bukkit.dependency.PluginIntegrationDependencyResolverSettings;
import com.wolfyscript.utilities.bukkit.world.items.reference.ItemCreateContext;
import com.wolfyscript.utilities.bukkit.world.items.reference.LegacyParser;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifierParser;
import com.wolfyscript.utilities.dependency.DependencyResolverSettings;
import me.wolfyscript.utilities.compatibility.plugins.ExecutableBlocksIntegration;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@KeyedStaticId(key = "executableblocks")
@DependencyResolverSettings(PluginIntegrationDependencyResolver.class)
@PluginIntegrationDependencyResolverSettings(pluginName = ExecutableBlocksIntegration.PLUGIN_NAME, integration = ExecutableBlocksIntegration.class)
public class ExecutableBlocksStackIdentifier implements StackIdentifier {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("executableblocks");

    private final ExecutableBlocksIntegration integration;
    private final ExecutableBlocksManager manager;
    private final String id;

    @JsonCreator
    public ExecutableBlocksStackIdentifier(@JsonProperty("id") String id) {
        this.integration = WolfyCoreBukkit.getInstance().getCompatibilityManager().getPlugins().getIntegration("ExecutableBlocks", ExecutableBlocksIntegration.class);
        this.manager = ExecutableBlocksManager.getInstance();
        this.id = id;
    }

    public ExecutableBlocksStackIdentifier(ExecutableBlocksIntegration integration, ExecutableBlocksManager manager, String id) {
        this.id = id;
        this.manager = manager;
        this.integration = integration;
    }

    private ExecutableBlocksStackIdentifier(ExecutableBlocksStackIdentifier other) {
        this.id = other.id;
        this.manager = other.manager;
        this.integration = other.integration;
    }

    @JsonGetter("id")
    public String getId() {
        return id;
    }

    @Override
    public ItemStack stack(ItemCreateContext context) {
        return manager.getExecutableBlock(id).map(eb -> eb.buildItem(context.amount(), context.player())).orElseGet(() -> new ItemStack(Material.AIR));
    }

    @Override
    public boolean matchesIgnoreCount(ItemStack other, boolean exact) {
        if (ItemUtils.isAirOrNull(other)) return false;
        return integration.getExecutableBlock(other).map(eB -> eB.equals(id)).orElse(false);
    }

    @Override
    public ExecutableBlocksRef convert(double weight, int amount) {
        ExecutableBlocksRef ref = new ExecutableBlocksRef(integration, manager, id);
        ref.setWeight(weight);
        ref.setAmount(amount);
        return ref;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return ID;
    }

    public static class Parser implements StackIdentifierParser<ExecutableBlocksStackIdentifier>, LegacyParser<ExecutableBlocksStackIdentifier> {

        private final ExecutableBlocksIntegration integration;
        private final ExecutableBlocksManager manager;

        public Parser(ExecutableBlocksIntegration integration, ExecutableBlocksManager manager) {
            this.integration = integration;
            this.manager = manager;
        }

        @Override
        public int priority() {
            return 0;
        }

        @Override
        public Optional<ExecutableBlocksStackIdentifier> from(ItemStack itemStack) {
            return integration.getExecutableBlock(itemStack).map(ebID -> new ExecutableBlocksStackIdentifier(integration, manager, ebID));
        }

        @Override
        public NamespacedKey getNamespacedKey() {
            return ID;
        }

        @Override
        public Optional<ExecutableBlocksStackIdentifier> from(JsonNode legacyData) {
            return Optional.of(new ExecutableBlocksStackIdentifier(integration, manager, legacyData.asText()));
        }
    }

}
