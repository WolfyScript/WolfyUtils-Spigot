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
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Objects;

public abstract class QueryNodePrimitive extends QueryNode {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("primitive");
    private Object value = null;

    @JsonCreator
    public QueryNodePrimitive(@JsonProperty("value") JsonNode valueNode, @JacksonInject("key") String key, @JacksonInject("path") String parentPath) {
        super(ID, key, parentPath);
        if (valueNode.isTextual()) {
            var text = valueNode.asText();
            if (!text.isBlank()) {
                char identifier = text.charAt(text.length() - 1);
                String value = text.substring(0, text.length() - 1);
                switch (identifier) {
                    case 'b', 'B' -> {
                        this.value = Byte.parseByte(value);
                        this.type = NBTType.NBTTagByte;
                    }
                    case 's', 'S' -> {
                        this.value = Short.parseShort(value);
                        this.type = NBTType.NBTTagShort;
                    }
                    case 'i', 'I' -> {
                        this.value = Integer.parseInt(value);
                        this.type = NBTType.NBTTagInt;
                    }
                    case 'l', 'L' -> {
                        this.value = Long.parseLong(value);
                        this.type = NBTType.NBTTagLong;
                    }
                    case 'f', 'F' -> {
                        this.value = Float.parseFloat(value);
                        this.type = NBTType.NBTTagFloat;
                    }
                    case 'd', 'D' -> {
                        this.value = Double.parseDouble(value);
                        this.type = NBTType.NBTTagDouble;
                    }
                    default -> {
                        this.value = value;
                        this.type = NBTType.NBTTagString;
                    }
                }
            }
        } else if (valueNode.isInt()) {
            this.value = valueNode.asInt(0);
            this.type = NBTType.NBTTagInt;
        } else if (valueNode.isDouble()) {
            this.value = valueNode.asDouble(0d);
            this.type = NBTType.NBTTagDouble;
        }
        Preconditions.checkArgument(!this.type.equals(NBTType.NBTTagEnd), "Error parsing primitive query node!");
    }

    @Override
    public boolean check(String key, NBTType type, NBTCompound parent) {
        if (this.key.equals(key) && this.type.equals(type)) {
            return Objects.equals(this.value, parent.getObject(key, value.getClass()));
        }
        return false;
    }
}
