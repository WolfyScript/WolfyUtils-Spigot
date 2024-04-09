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

package com.wolfyscript.utilities.bukkit.json.serialization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wolfyscript.utilities.WolfyCore;
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.spigot.WolfyCoreSpigot;
import com.wolfyscript.utilities.config.jackson.JacksonUtil;
import java.util.Map;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.Yaml;

public class ItemStackSerialization {

    public static void create(SimpleModule module){
        JacksonUtil.addSerializerAndDeserializer(module, ItemStack.class, (itemStack, gen, serializerProvider) -> {
            if (itemStack != null) {
                var yaml = new Yaml();
                var config = new YamlConfiguration();
                config.set("i", itemStack);
                Map<String, Object> map = yaml.load(config.saveToString());
                gen.writeObject(map.get("i"));
            }
        }, (p, deserializationContext) -> {
            JsonNode node = p.readValueAsTree();
            if (node.isValueNode()) {
                //Old Serialization Methods. like Base64 or NMS serialization
                String value = node.asText();
                if (!value.startsWith("{")) {
                    return ((WolfyCoreCommon) WolfyCore.getInstance()).getWolfyUtils().getNmsUtil().getItemUtil().getBase64ItemStack(value);
                }
                return value.equals("empty") ? null : ((WolfyCoreCommon) WolfyCore.getInstance()).getWolfyUtils().getNmsUtil().getItemUtil().getJsonItemStack(value);
            }
            var config = new YamlConfiguration();
            //Loads the Map from the JsonNode && Sets the Map to YamlConfig
            config.set("i", p.getCodec().readValue(node.traverse(p.getCodec()), new TypeReference<Map<String, Object>>() {
            }));
            try {
                    /*
                    Load new YamlConfig from just saved string.
                    That will convert the Map to an ItemStack!
                     */
                config.loadFromString(config.saveToString());
                return config.getItemStack("i");
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
