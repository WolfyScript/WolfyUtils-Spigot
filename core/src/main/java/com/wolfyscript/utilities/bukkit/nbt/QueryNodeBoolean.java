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
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Optional;

public class QueryNodeBoolean extends QueryNode<Object> {

    public static final NamespacedKey TYPE = NamespacedKey.wolfyutilties("boolean");

    private final boolean value;

    @JsonCreator
    public QueryNodeBoolean(@JsonProperty("value") boolean value, @JacksonInject("key") String key, @JacksonInject("parent_path") String parentPath) {
        super(TYPE, key, parentPath);
        this.value = value;
    }

    @Override
    public boolean check(String key, NBTType type, NBTCompound parent) {
        return this.key.equals(key) && value;
    }

    //TODO: Make this work for all types of values and apply the values accordingly
    @Override
    protected Optional<Object> readValue(String path, String key, NBTCompound parent) {
        var type = parent.getType(key);
        return Optional.ofNullable(switch (type) {
            case NBTTagInt -> parent.getInteger(key);
            case NBTTagByte -> parent.getByte(key);
            case NBTTagShort -> parent.getShort(key);
            case NBTTagLong -> parent.getLong(key);
            case NBTTagDouble -> parent.getDouble(key);
            case NBTTagFloat -> parent.getFloat(key);
            case NBTTagString -> parent.getString(key);
            case NBTTagCompound -> parent.getCompound(key);
            default -> null;
        });
    }

    @Override
    protected void applyValue(String path, String key, Object value, NBTCompound resultContainer) {
        switch (value.getClass().getSimpleName()) {
            case "Integer" -> resultContainer.setInteger(key, (int) value);
            default -> resultContainer.setString(key, (String) value);
        }
    }
}
