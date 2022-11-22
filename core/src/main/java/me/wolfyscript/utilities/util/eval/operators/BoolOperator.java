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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.wolfyscript.utilities.eval.operator.BoolOperatorConst;
import java.io.IOException;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.json.jackson.annotations.KeyedBaseType;
import me.wolfyscript.utilities.util.json.jackson.annotations.OptionalValueDeserializer;

/**
 * An Operator that evaluates into a booleanish value.
 */
@KeyedBaseType(baseType = me.wolfyscript.utilities.util.eval.operators.Operator.class)
@OptionalValueDeserializer(delegateObjectDeserializer = true, deserializer = BoolOperator.OptionalValueDeserializer.class)
public abstract class BoolOperator extends Operator {

    public BoolOperator(NamespacedKey namespacedKey) {
        super(namespacedKey);
    }

    public abstract boolean evaluate(EvalContext context);

    public static class OptionalValueDeserializer extends me.wolfyscript.utilities.util.json.jackson.ValueDeserializer<BoolOperator> {

        public OptionalValueDeserializer() {
            super(BoolOperator.class);
        }

        @Override
        public BoolOperator deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.readValueAsTree();
            if (node.isObject()) return null;
            return new BoolOperatorConst(node.asBoolean());
        }
    }
}
