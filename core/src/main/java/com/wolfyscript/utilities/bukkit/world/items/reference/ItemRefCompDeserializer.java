package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import com.wolfyscript.utilities.bukkit.world.items.references.APIReference;
import com.wolfyscript.utilities.common.WolfyUtils;
import java.io.IOException;

public class ItemRefCompDeserializer extends StdNodeBasedDeserializer<ItemReference> {

    protected ItemRefCompDeserializer() {
        super(ItemReference.class);
    }

    @Override
    public ItemReference convert(JsonNode root, DeserializationContext ctxt) throws IOException {
        if (root.has("type")) {
            // New ItemReference used! No conversion required!
            return ctxt.readTreeAsValue(root, ItemReference.class);
        }
        // Need to convert APIReference
        APIReference apiReference = ctxt.readTreeAsValue(root, APIReference.class);
        return new BackwardsWrapperReference((WolfyUtils) ctxt.findInjectableValue(WolfyUtils.class.getName(), null, null), apiReference);
    }
}
