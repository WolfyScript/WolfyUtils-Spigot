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

package com.wolfyscript.utilities.bukkit.world.items.meta;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.world.inventory.item_builder.ItemBuilder;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import com.wolfyscript.utilities.config.jackson.KeyedTypeIdResolver;
import com.wolfyscript.utilities.config.jackson.KeyedTypeResolver;
import com.wolfyscript.utilities.config.jackson.JacksonUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "key")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder("key")
public abstract class Meta implements Keyed {

    private final NamespacedKey key;

    protected MetaSettings.Option option = MetaSettings.Option.EXACT;
    @JsonIgnore
    private List<MetaSettings.Option> availableOptions = List.of(MetaSettings.Option.EXACT);

    protected Meta(NamespacedKey key) {
        this.key = key;
    }

    public MetaSettings.Option getOption() {
        return option;
    }

    public void setOption(MetaSettings.Option option) {
        this.option = option;
    }

    @JsonIgnore
    public boolean isExact() {
        return option.equals(MetaSettings.Option.EXACT);
    }

    public List<MetaSettings.Option> getAvailableOptions() {
        return availableOptions;
    }

    protected void setAvailableOptions(MetaSettings.Option... options) {
        if (options != null) {
            availableOptions = Arrays.asList(options);
        }
    }

    @Deprecated
    public boolean check(ItemBuilder itemOther, ItemBuilder item) {
        return true;
    }

    public abstract boolean check(CustomItem item, ItemBuilder itemOther);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meta meta = (Meta) o;
        return Objects.equals(key, meta.key) && option == meta.option && Objects.equals(availableOptions, meta.availableOptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, option, availableOptions);
    }

    @JsonIgnore
    @Override
    public final NamespacedKey key() {
        return key;
    }

    @Deprecated
    public static class Provider<M extends Meta> implements Keyed {

        private final NamespacedKey namespacedKey;
        private final Class<M> type;

        public Provider(NamespacedKey namespacedKey, @NotNull Class<M> type) {
            Objects.requireNonNull(type, "Cannot initiate Meta \"" + namespacedKey.toString() + "\" with a null type!");
            this.namespacedKey = namespacedKey;
            this.type = type;
        }

        public NamespacedKey key() {
            return namespacedKey;
        }

        public M provide() {
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Deprecated
        public M parse(JsonNode node) {
            return JacksonUtil.getObjectMapper().convertValue(node, type);
        }

    }


}
