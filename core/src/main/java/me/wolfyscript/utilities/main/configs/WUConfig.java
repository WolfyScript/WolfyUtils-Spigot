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

package me.wolfyscript.utilities.main.configs;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.config.YamlConfiguration;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class WUConfig extends YamlConfiguration {

    public WUConfig(ConfigAPI configAPI, WolfyUtilCore plugin) {
        super(configAPI, plugin.getDataFolder().getPath(), "config", "", "config", false);
    }

    public boolean isAPIReferenceEnabled(APIReference.Parser<?> parser) {
        return getBoolean("api_references." + parser.getId(), true);
    }

    public Map<NamespacedKey, Integer> getIdentifierParserPriorities() {
        ConfigurationSection section = getConfigurationSection("stack_identifiers.priorities");
        if (section == null) return Map.of();

        Set<String> keys = section.getKeys(false);
        if (keys.isEmpty()) return Map.of();

        return keys.stream().map(key -> {
            int priority = section.getInt(key, 0);
            NamespacedKey namespacedKey = NamespacedKey.of(key);
            if (namespacedKey == null) {
                plugin.getLogger().warning("Cannot load priority for stack identifier \" " + key + "\"! Invalid key format (<namespace>:<name>)!");
                return null;
            }
            return Map.entry(namespacedKey, priority);
        }).filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
