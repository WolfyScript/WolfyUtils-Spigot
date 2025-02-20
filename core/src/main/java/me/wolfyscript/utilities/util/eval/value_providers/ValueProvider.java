/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.util.eval.value_providers;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeResolver;
import me.wolfyscript.utilities.util.json.jackson.annotations.OptionalValueDeserializer;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@OptionalValueDeserializer(deserializer = ValueProvider.ValueDeserializer.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "key")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = {"key"})
public interface ValueProvider<V> extends Keyed {

    @JsonIgnore
    V getValue(EvalContext context);

    @JsonIgnore
    default V getValue() {
        return getValue(new EvalContext());
    }

    @JsonGetter("key")
    @Override
    NamespacedKey getNamespacedKey();

    class ValueDeserializer extends me.wolfyscript.utilities.util.json.jackson.ValueDeserializer<ValueProvider<?>> {

        private static final Pattern NUM_PATTERN = Pattern.compile("([0-9]+)([bBsSiIlL])|([0-9]?\\.?[0-9])+([fFdD])");

        public ValueDeserializer() {
            super((Class<ValueProvider<?>>)(Object) ValueProvider.class);
        }

        @Override
        public ValueProvider<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (p.currentToken() == JsonToken.VALUE_STRING) {
                JsonNode node = p.readValueAsTree();
                String text = node.asText();
                if (!text.isBlank()) {
                    Matcher matcher = NUM_PATTERN.matcher(text);
                    if (matcher.matches()) {
                        String value;
                        String id = matcher.group(2);
                        if (id != null) {
                            // integer value
                            value = matcher.group(1);
                        } else {
                            // float value
                            id = matcher.group(4);
                            value = matcher.group(3);
                        }
                        try {
                            return switch (id.charAt(0)) {
                                case 'i', 'I' -> new ValueProviderIntegerConst(Integer.parseInt(value));
                                case 'f', 'F' -> new ValueProviderFloatConst(Float.parseFloat(value));
                                default -> new ValueProviderStringConst(text);
                            };
                        } catch (NumberFormatException e) {
                            // Cannot parse the value. Might a String value!
                        }
                    }
                    return new ValueProviderStringConst(text);
                }
            } else if (p.currentToken() == JsonToken.VALUE_NUMBER_INT) {
                return new ValueProviderIntegerConst(ctxt.readValue(p, Integer.class));
            }
            // Ignore floating point values as they are read as double value providers, which are not available here, in v5.
            return null;
        }
    }
}
