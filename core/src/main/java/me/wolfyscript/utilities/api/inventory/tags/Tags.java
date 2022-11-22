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

package me.wolfyscript.utilities.api.inventory.tags;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.common.registry.AbstractRegistry;
import java.util.HashMap;
import java.util.Map;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import com.wolfyscript.utilities.bukkit.registry.BukkitRegistries;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import org.jetbrains.annotations.Nullable;

public class Tags<T extends Keyed> extends AbstractRegistry<Map<NamespacedKey, CustomTag<T>>, CustomTag<T>> {

    @Deprecated
    public Tags() {
        super(new BukkitNamespacedKey(WolfyUtilCore.getInstance(), "custom_tags"), HashMap::new, WolfyUtilCore.getInstance().getRegistries());
    }

    public Tags(BukkitRegistries registries) {
        super(registries.getCore().getWolfyUtils().getIdentifiers().getWolfyUtilsNamespaced("custom_tags"), HashMap::new, registries);
    }

    @Nullable
    public CustomTag<T> getTag(BukkitNamespacedKey namespacedKey) {
        return get(namespacedKey);
    }

}
