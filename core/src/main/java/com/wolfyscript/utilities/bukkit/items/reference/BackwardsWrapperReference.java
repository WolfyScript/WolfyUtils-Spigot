package com.wolfyscript.utilities.bukkit.items.reference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

// Don't use it to parse from ItemStack (Set priority lower than BukkitItemReference)!
@ItemReferenceParserSettings(priority = -999, customParser = BackwardsWrapperReference.BackwardCompatibleParser.class)
public class BackwardsWrapperReference extends ItemReference {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("backwards_comp_wrapper");

    private final APIReference apiReference;

    public BackwardsWrapperReference(APIReference apiReference) {
        super(ID);
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

    public static class BackwardCompatibleParser extends Parser<BackwardsWrapperReference> {

        private static final Map<String, APIReference.Parser<?>> PARSERS = new HashMap<>();

        protected BackwardCompatibleParser(int priority) {
            super(BackwardsWrapperReference.ID, priority, BackwardsWrapperReference.class);
        }

        /**
         * Register a new {@link APIReference.Parser} that can parse ItemStacks and keys from another plugin to a usable {@link APIReference}
         *
         * @param parser an {@link APIReference.Parser} instance.
         */
        public static void registerAPIReferenceParser(APIReference.Parser<?> parser) {
            if (parser instanceof APIReference.PluginParser<?> pluginParser) {
                if (!WolfyUtilities.hasPlugin(pluginParser.getPluginName())) {
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

        @Override
        public BackwardsWrapperReference parseFromStack(ItemStack stack) {
            return new BackwardsWrapperReference(parseOldAPIRef(stack));
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
    }
}
