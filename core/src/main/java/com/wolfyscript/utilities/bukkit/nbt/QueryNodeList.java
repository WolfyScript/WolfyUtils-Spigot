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

import com.fasterxml.jackson.annotation.*;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTList;
import de.tr7zw.nbtapi.NBTType;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.List;
import java.util.Optional;

public abstract class QueryNodeList<VAL> extends QueryNode<NBTList<VAL>> {

    @JsonIgnore
    private final Class<VAL> elementType;
    private final List<Element<VAL>> elements;

    @JsonCreator
    public QueryNodeList(NamespacedKey type, @JsonProperty("elements") List<Element<VAL>> elements, @JacksonInject("key") String key, @JacksonInject("parent_path") String path, NBTType elementType, Class<VAL> elementClass) {
        super(type, key, path);
        this.elementType = elementClass;
        this.nbtType = elementType;
        this.elements = elements;
    }

    protected QueryNodeList(QueryNodeList<VAL> other) {
        super(other.type, other.key, other.parentPath);
        this.nbtType = other.nbtType;
        this.elementType = other.elementType;
        this.elements = other.elements.stream().map(Element::copy).toList();
    }

    public List<Element<VAL>> getElements() {
        return elements;
    }

    @Override
    public boolean check(String key, NBTType nbtType, NBTList<VAL> value) {
        return !value.isEmpty();
    }

    @Override
    protected Optional<NBTList<VAL>> readValue(String path, String key, NBTCompound parent) {
        return Optional.ofNullable(readList(key, parent));
    }

    @Override
    protected void applyValue(String path, String key, NBTList<VAL> value, NBTCompound resultContainer) {
        NBTList<VAL> list = readList(key, resultContainer);
        if (list != null && !value.isEmpty()) {
            for (Element<VAL> element : elements) {
                element.index().ifPresentOrElse(index -> {
                    if (index < 0) {
                        index = value.size() + (index % value.size()); //Convert the negative index to a positive reverted index, that starts from the end.
                    }
                    index = index % value.size(); //Prevent out of bounds
                    if (value.size() > index) {
                        int fIndex = index;
                        element.value().ifPresentOrElse(queryNode -> queryNode.visit(path, fIndex, value, list), () -> list.add(value.get(fIndex)));
                    }
                }, () -> element.value().ifPresent(valQueryNode -> {
                    for (int i = 0; i < value.size(); i++) {
                        valQueryNode.visit(path, i, value, list);
                    }
                }));
            }
        }
    }

    protected NBTList<VAL> readList(String key, NBTCompound container) {
        if (elementType == Integer.class) {
            return (NBTList<VAL>) container.getIntegerList(key);
        } else if (elementType == Long.class) {
            return (NBTList<VAL>) container.getLongList(key);
        } else if (elementType == Double.class) {
            return (NBTList<VAL>) container.getDoubleList(key);
        } else if (elementType == Float.class) {
            return (NBTList<VAL>) container.getFloatList(key);
        } else if (elementType == String.class) {
            return (NBTList<VAL>) container.getStringList(key);
        } else if (elementType == NBTCompound.class) {
            return (NBTList<VAL>) container.getCompoundList(key);
        } else if (elementType == int[].class) {
            return (NBTList<VAL>) container.getIntArrayList(key);
        }
        return null;
    }

    public static class Element<VAL> {

        private Integer index;
        private QueryNode<VAL> value;

        public Element() {
            this.index = null;
            this.value = null;
        }

        private Element(Element<VAL> other) {
            this.index = other.index;
            this.value = other.value.copy();
        }

        private Optional<Integer> index() {
            return Optional.ofNullable(index);
        }

        private Optional<QueryNode<VAL>> value() {
            return Optional.ofNullable(value);
        }

        @JsonSetter
        public void setIndex(int index) {
            this.index = index;
        }

        @JsonGetter
        private int getIndex() {
            return index;
        }

        @JsonSetter
        public void setValue(QueryNode<VAL> value) {
            this.value = value;
        }

        @JsonGetter
        public QueryNode<VAL> getValue() {
            return value;
        }

        public Element<VAL> copy() {
            return new Element<>(this);
        }
    }
}
