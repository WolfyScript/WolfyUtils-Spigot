package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.util.NamespacedKey;

/**
 * Used to keep track of old {@link me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference} data from the config.
 * Because APIReferences did not store the original ItemStack in the config, the StackIdentifiers need to be parsed from the old data instead.
 */
public class LegacyStackReference extends StackReference {

    private final JsonNode data;

    public LegacyStackReference(WolfyUtilCore core, NamespacedKey parserKey, double weight, int amount, JsonNode data) {
        super(core, parserKey, weight, amount, null);
        this.data = data;
    }

    public JsonNode data() {
        return data;
    }

    @Override
    protected StackIdentifier parseIdentifier() {
        if (identifier != null) return identifier;
        if (parser() instanceof LegacyParser<?> legacyParser) {
            return legacyParser.from(data).orElse(null);
        }
        return super.parseIdentifier();
    }
}
