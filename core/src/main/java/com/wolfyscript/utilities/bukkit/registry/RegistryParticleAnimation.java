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

package com.wolfyscript.utilities.bukkit.registry;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.world.particles.ParticleAnimation;
import com.wolfyscript.utilities.registry.AbstractRegistry;
import java.util.HashMap;
import java.util.Map;

public class RegistryParticleAnimation extends AbstractRegistry<Map<NamespacedKey, ParticleAnimation>, ParticleAnimation> {

    RegistryParticleAnimation(BukkitRegistries registries) {
        super(registries.getCore().getWolfyUtils().getIdentifiers().getWolfyUtilsNamespaced("particle_animations"), HashMap::new, registries, ParticleAnimation.class);
    }

    @Override
    public void register(NamespacedKey namespacedKey, ParticleAnimation value) {
        super.register(namespacedKey, value);
        value.setKey(namespacedKey);
    }

}
