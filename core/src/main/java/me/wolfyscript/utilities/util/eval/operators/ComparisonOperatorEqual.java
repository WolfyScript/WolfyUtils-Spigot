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
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProvider;

public class ComparisonOperatorEqual<V extends Comparable<V>> extends ComparisonOperator<V> {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("equal");

    @JsonCreator
    protected ComparisonOperatorEqual(@JsonProperty("this") ValueProvider<V> thisValue, @JsonProperty("that") ValueProvider<V> thatValue) {
        super(KEY, thisValue, thatValue);
    }

    @Override
    public boolean evaluate(EvalContext context) {
        return this.thisValue.getValue(context).compareTo(this.thatValue.getValue(context)) == 0;
    }
}
