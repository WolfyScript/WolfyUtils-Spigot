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

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class QueryNodeObject extends QueryNode<NBTCompound> {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("object");

    //If include is true it includes this node with each and every child node.
    private boolean include = false;
    //If includes has values it includes this node with the specified child nodes.
    private Map<String, Boolean> includes;
    //Checks and verifies the child nodes. This node is only included if all the child nodes are valid.
    private Map<String, QueryNode<?>> requiredChildNodes;
    //Child nodes to proceed to next. This is useful for further child compound tag settings.
    @JsonIgnore
    private Map<String, QueryNode<?>> subNodes;

    @JsonCreator
    public QueryNodeObject(ObjectNode node, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(ID, key, parentPath);
        this.nbtType = NBTType.NBTTagCompound;



    }

    @JsonAnySetter
    public void setSubNodes(Map<String, QueryNode<?>> subNodes) {
        this.subNodes = subNodes;
    }

    @JsonAnyGetter
    public Map<String, QueryNode<?>> getSubNodes() {
        return subNodes;
    }

    @JsonSetter("required")
    public void setRequiredChildNodes(Map<String, QueryNode<?>> requiredChildNodes) {
        this.requiredChildNodes = requiredChildNodes;
    }

    @JsonGetter("required")
    public Map<String, QueryNode<?>> getRequiredChildNodes() {
        return requiredChildNodes;
    }

    @Override
    public boolean check(String key, NBTType type, NBTCompound parent) {
        return false;
    }

    @Override
    protected Optional<NBTCompound> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(parent.getCompound(key));
    }

    @Override
    public void applyValue(String path, String key, NBTCompound value, NBTCompound resultContainer) {
        Set<String> keys;
        String newPath = path + "." + key;
        NBTCompound container = resultContainer;
        if (!include && includes.isEmpty()) {
            keys = value.getKeys().stream().filter(s -> includes.get(s)).collect(Collectors.toSet());
            //Nothing to include proceed to children
        } else {
            //Add this container to the result if included
            container = resultContainer.addCompound(key);
            keys = value.getKeys();
        }
        //Process child nodes with the specified settings.
        for (String childKey : keys) {
            QueryNode<?> subQueryNode = getSubNodes().get(childKey);
            if (subQueryNode != null) {
                subQueryNode.visit(newPath, childKey, value, container);
            }
        }
    }

}
