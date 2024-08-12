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

package me.wolfyscript.utilities.util.json.jackson.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.NamespacedKey;

public class BukkitNamespacedKeySerialization {

    public static void create(SimpleModule module) {
        JacksonUtil.addSerializerAndDeserializer(
                module,
                NamespacedKey.class,
                (value, gen, serializerProvider) -> gen.writeString(value.toString()),
                (p, deserializationContext) -> {
                    JsonNode node = p.readValueAsTree();
                    if (node.isObject()) {
                        String namespace = node.get("namespace").asText();
                        String key = node.get("key").asText();
                        return new NamespacedKey(namespace, key);
                    }
                    if (node.isTextual()) {
                        return NamespacedKey.fromString(node.asText());
                    }
                    WolfyUtilities.getWUCore().getConsole().warn("Error Deserializing NamespacedKey! Must be an object with 'namespace' and 'key' property, or a string value of the format '<namespace>:<key>'");
                    return null;
                });
    }
}
