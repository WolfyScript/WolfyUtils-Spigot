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
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.Color;
import org.bukkit.Particle;

public class DustOptionsSerialization {

    public static void create(SimpleModule module) {
        JacksonUtil.addSerializerAndDeserializer(module, Particle.DustOptions.class, (dustOptions, gen, s) -> {
            gen.writeStartObject();
            gen.writeNumberField("size", dustOptions.getSize());
            gen.writeObjectField("color", dustOptions.getColor());
            gen.writeEndObject();
        }, (p, ctxt) -> {
            JsonNode node = p.readValueAsTree();
            if (node.isObject()) {
                float size = node.get("size").floatValue();
                Color color = p.getCodec().treeToValue(node.get("color"), Color.class);
                return new Particle.DustOptions(color, size);
            }
            WolfyCoreBukkit.getInstance().getWolfyUtils().getConsole().warn("Error Deserializing DustOptions! Invalid DustOptions object!");
            return null;
        });
    }
}
