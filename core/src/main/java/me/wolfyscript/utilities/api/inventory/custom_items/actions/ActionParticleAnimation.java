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

package me.wolfyscript.utilities.api.inventory.custom_items.actions;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import me.wolfyscript.utilities.util.particles.ParticleAnimation;

public class ActionParticleAnimation extends Action<me.wolfyscript.utilities.api.inventory.custom_items.actions.DataLocation> {

    public static final BukkitNamespacedKey KEY = BukkitNamespacedKey.wolfyutilties("location/particle_animation");

    private ParticleAnimation animation;

    protected ActionParticleAnimation() {
        super(KEY, me.wolfyscript.utilities.api.inventory.custom_items.actions.DataLocation.class);
    }

    @Override
    public void execute(WolfyUtilCore core, DataLocation data) {
        if (data instanceof DataPlayer dataPlayer) {
            animation.spawn(dataPlayer.getPlayer());
            return;
        }
        animation.spawn(data.getLocation());
    }

    public void setAnimation(ParticleAnimation animation) {
        this.animation = animation;
    }
}
