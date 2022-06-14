/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
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

package com.wolfyscript.utilities.bukkit.nbt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

import java.io.IOException;
import java.util.Map;

public class NBTQuery {

    private Map<String, QueryNode> nodes;

    @JsonCreator
    public NBTQuery(ObjectNode node) {
        ObjectMapper objMapper = JacksonUtil.getObjectMapper();
        node.fields().forEachRemaining(entry -> {
            var key = entry.getKey();
            var injectVars = new InjectableValues.Std();
            injectVars.addValue("key", key);
            injectVars.addValue("path", "");
            var subNode = entry.getValue();
            try {
                QueryNode queryNode = objMapper.reader(injectVars).readValue(subNode, QueryNode.class);
                if (queryNode != null) {
                    nodes.put(key, queryNode);
                }
            } catch (IOException e) {

            }
        });

    }



    public NBTCompound find() {
        NBTContainer container = new NBTContainer();






        return (NBTCompound) new NBTContainer();
    }


}
