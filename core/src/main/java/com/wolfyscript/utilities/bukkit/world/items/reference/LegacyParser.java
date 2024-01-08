package com.wolfyscript.utilities.bukkit.world.items.reference;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

public interface LegacyParser<T extends StackIdentifier> {

    Optional<T> from(JsonNode legacyData);

}
