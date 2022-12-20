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


import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.world.inventory.item_builder.ItemBuilder;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import java.util.Objects;
import org.bukkit.inventory.meta.ItemMeta;

public class PotionMeta extends Meta {

    public static final BukkitNamespacedKey KEY = BukkitNamespacedKey.wolfyutilties("potion");

    public PotionMeta() {
        super(KEY);
    }

    @Override
    public boolean check(CustomItem item, ItemBuilder itemOther) {
        ItemMeta meta1 = itemOther.getItemMeta();
        ItemMeta meta2 = item.getItemMeta();
        if (meta2 instanceof org.bukkit.inventory.meta.PotionMeta) {
            if (meta1 instanceof org.bukkit.inventory.meta.PotionMeta) {
                org.bukkit.inventory.meta.PotionMeta metaThat = (org.bukkit.inventory.meta.PotionMeta) itemOther.getItemMeta();
                org.bukkit.inventory.meta.PotionMeta metaThis = (org.bukkit.inventory.meta.PotionMeta) item.getItemMeta();
                if (metaThis.getBasePotionData().getType().equals(metaThat.getBasePotionData().getType())) {
                    if (metaThis.hasCustomEffects()) {
                        if (!metaThat.hasCustomEffects() || !metaThis.getCustomEffects().equals(metaThat.getCustomEffects())) {
                            return false;
                        }
                    } else if (metaThat.hasCustomEffects()) {
                        return false;
                    }
                    if (metaThis.hasColor()) {
                        return metaThat.hasColor() && Objects.equals(metaThis.getColor(), metaThat.getColor());
                    } else return !metaThat.hasColor();
                }
            }
            return false;
        }
        return !(meta1 instanceof org.bukkit.inventory.meta.PotionMeta);
    }
}
