package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.wolfyscript.utilities.KeyedStaticId;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilCore;
import com.wolfyscript.utilities.bukkit.world.items.references.APIReference;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

// Don't use it to parse from ItemStack (Set priority lower than BukkitItemReference)!
@ItemReferenceParserSettings(priority = -999, parser = BackwardsWrapperReference.BackwardCompatibleParser.class)
@KeyedStaticId(key = "backwards_comp_wrapper")
public class BackwardsWrapperReference extends ItemReference {

    private final APIReference apiReference;

    public BackwardsWrapperReference(WolfyUtils wolfyUtils, APIReference apiReference) {
        super(wolfyUtils);
        this.apiReference = apiReference;
    }

    public BackwardsWrapperReference(BackwardsWrapperReference reference) {
        super(reference);
        this.apiReference = reference.apiReference.clone();
    }

    public APIReference getWrappedApiReference() {
        return apiReference;
    }

    @Override
    public ItemReference copy() {
        return new BackwardsWrapperReference(this);
    }

    @Override
    public ItemStack getItem() {
        return apiReference.getLinkedItem();
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        return apiReference.isValidItem(itemStack);
    }

    public static class BackwardCompatibleParser implements Parser<BackwardsWrapperReference> {

        private static final Map<String, APIReference.Parser<?>> PARSERS = new HashMap<>();

        @Override
        public Optional<BackwardsWrapperReference> parseFromStack(WolfyUtils wolfyUtils, ItemStack stack) {
            return Optional.of(new BackwardsWrapperReference(wolfyUtils, parseOldAPIRef(stack)));
        }

        private APIReference parseOldAPIRef(ItemStack stack) {
            if (stack != null) {
                APIReference apiReference = PARSERS.values().stream().sorted(APIReference.Parser::compareTo).map(parser -> parser.construct(stack)).filter(Objects::nonNull).findFirst().orElse(null);
                if (apiReference != null) {
                    apiReference.setAmount(stack.getAmount());
                    return apiReference;
                }
            }
            return null;
        }

        /**
         * Register a new {@link APIReference.Parser} that can parse ItemStacks and keys from another plugin to a usable {@link APIReference}
         *
         * @param parser an {@link APIReference.Parser} instance.
         */
        public static void registerAPIReferenceParser(APIReference.Parser<?> parser) {
            if (parser instanceof APIReference.PluginParser<?> pluginParser) {
                if (!WolfyUtilCore.getInstance().getCompatibilityManager().getPlugins().isPluginEnabled(pluginParser.getPluginName())) {
                    return;
                }
                pluginParser.init(Bukkit.getPluginManager().getPlugin(pluginParser.getPluginName()));
            }
            PARSERS.put(parser.getId(), parser);
            if (!parser.getAliases().isEmpty()) {
                parser.getAliases().forEach(s -> PARSERS.putIfAbsent(s, parser));
            }
        }

        @Nullable
        public static APIReference.Parser<?> getApiReferenceParser(String id) {
            return PARSERS.get(id);
        }
    }
}
