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
import com.fasterxml.jackson.databind.JsonNode;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.List;
import java.util.Optional;

public class QueryNodeList<VAL> extends QueryNode<List<VAL>> {

    public static final NamespacedKey ID = NamespacedKey.wolfyutilties("list");

    private int index;
    private List<QueryNode<VAL>> values;

    @JsonCreator
    public QueryNodeList(JsonNode node, @JacksonInject("key") String key, @JacksonInject("path") String path, NBTType type) {
        super(ID, key, path);
        this.nbtType = type;
    }

    @Override
    public boolean check(String key, NBTType type, NBTCompound parent) {
        return false;
    }

    @Override
    protected Optional<List<VAL>> readValue(String path, String key, NBTCompound parent) {
        return Optional.empty();
    }

    @Override
    protected void applyValue(String path, String key, List<VAL> value, NBTCompound resultContainer) {

    }

    public int getIndex() {
        return index;
    }


}
