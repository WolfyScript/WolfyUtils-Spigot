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
import de.tr7zw.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.annotations.KeyedBaseType;

import java.util.Objects;

@KeyedBaseType(baseType = QueryNode.class)
public abstract class QueryNodePrimitive<VAL> extends QueryNode<VAL> {

    protected final VAL value;

    @JsonCreator
    protected QueryNodePrimitive(NamespacedKey type, VAL value, @JacksonInject("key") String key, @JacksonInject("path") String parentPath) {
        super(type, key, parentPath);
        this.value = value;
    }

    protected QueryNodePrimitive(QueryNodePrimitive<VAL> other) {
        super(other.type, other.key, other.parentPath);
        this.nbtType = other.nbtType;
        this.value = other.value;
    }

    @Override
    public boolean check(String key, NBTType nbtType, VAL value) {
        return Objects.equals(this.value, value);
    }
}
