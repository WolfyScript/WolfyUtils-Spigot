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

package me.wolfyscript.utilities.registry;

import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple registry, used for basic use cases.
 *
 * @param <V> The type of the value.
 */
public class RegistrySimple<V extends Keyed> extends AbstractRegistry<Map<NamespacedKey, V>, V> {

    public RegistrySimple(NamespacedKey namespacedKey, Registries registries) {
        super(namespacedKey, new HashMap<>(), registries);
    }

    public RegistrySimple(NamespacedKey namespacedKey, Registries registries, Class<V> type) {
        super(namespacedKey, new HashMap<>(), registries, type);
    }
}
