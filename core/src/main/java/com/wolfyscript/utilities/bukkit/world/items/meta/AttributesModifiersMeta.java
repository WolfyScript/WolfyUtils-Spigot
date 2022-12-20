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


import com.google.common.collect.Multimap;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.world.inventory.item_builder.ItemBuilder;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.meta.ItemMeta;

public class AttributesModifiersMeta extends Meta {

    public static final BukkitNamespacedKey KEY = BukkitNamespacedKey.wolfyutilties("attributes_modifiers");

    public AttributesModifiersMeta() {
        super(KEY);
    }

    private static boolean compareModifiers(Multimap<Attribute, AttributeModifier> first, Multimap<Attribute, AttributeModifier> second) {
        if (first != null && second != null) {
            return first.entries().stream().allMatch(entry -> second.containsEntry(entry.getKey(), entry.getValue())) && second.entries().stream().allMatch(entry -> first.containsEntry(entry.getKey(), entry.getValue()));
        } else {
            return false;
        }
    }

    @Override
    public boolean check(CustomItem item, ItemBuilder itemOther) {
        ItemMeta metaOther = itemOther.getItemMeta();
        ItemMeta meta = item.getItemMeta();
        if (meta.hasAttributeModifiers()) {
            return metaOther.hasAttributeModifiers() && compareModifiers(meta.getAttributeModifiers(), metaOther.getAttributeModifiers());
        }
        return !metaOther.hasAttributeModifiers();
    }
}
