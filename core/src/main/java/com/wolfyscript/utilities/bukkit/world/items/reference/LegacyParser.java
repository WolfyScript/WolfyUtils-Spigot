package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

/**
 * Marks a legacy StackIdentifier Parser, that parses the StackIdentifier from the old {@link me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference} data "format".
 *
 * @param <T> The type of the StackIdentifier
 */
public interface LegacyParser<T extends StackIdentifier> {

    Optional<T> from(JsonNode legacyData);

}
