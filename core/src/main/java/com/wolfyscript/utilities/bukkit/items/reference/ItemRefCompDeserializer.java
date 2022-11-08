package com.wolfyscript.utilities.bukkit.items.reference;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import java.io.IOException;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;

public class ItemRefCompDeserializer extends StdNodeBasedDeserializer<ItemReference> {

    protected ItemRefCompDeserializer() {
        super(ItemReference.class);
    }

    @Override
    public ItemReference convert(JsonNode root, DeserializationContext ctxt) throws IOException {
        if (root.has("id")) {
            // New ItemReference used! No conversion required!
            return ctxt.readTreeAsValue(root, ItemReference.class);
        }
        // Need to convert APIReference
        APIReference apiReference = ctxt.readTreeAsValue(root, APIReference.class);
        return new BackwardsWrapperReference(apiReference);
    }
}
