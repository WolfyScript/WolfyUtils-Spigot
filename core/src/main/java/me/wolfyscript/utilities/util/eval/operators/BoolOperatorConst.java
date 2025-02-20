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

package me.wolfyscript.utilities.util.eval.operators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.json.jackson.annotations.OptionalValueSerializer;

import java.io.IOException;

@OptionalValueSerializer(serializer = BoolOperatorConst.ValueSerializer.class)
public class BoolOperatorConst extends BoolOperator {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("bool/const");

    private final boolean value;

    @JsonCreator
    public BoolOperatorConst(@JsonProperty("value") boolean value) {
        super(NamespacedKey.wolfyutilties("bool/const"));
        this.value = value;
    }

    @Override
    public boolean evaluate(EvalContext context) {
        return value;
    }

    public static class ValueSerializer extends me.wolfyscript.utilities.util.json.jackson.ValueSerializer<BoolOperatorConst> {

        public ValueSerializer() {
            super(BoolOperatorConst.class);
        }

        @Override
        public boolean serialize(BoolOperatorConst targetObject, JsonGenerator generator, SerializerProvider provider) throws IOException {
            generator.writeBoolean(targetObject.value);
            return true;
        }
    }
}
