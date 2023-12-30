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

package com.wolfyscript.utilities.bukkit.world.items.actions;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.WolfyUtils;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

public class ActionSound extends Action<DataLocation> {

    public static final BukkitNamespacedKey KEY = BukkitNamespacedKey.wolfyutilties("location/sound");

    private Sound sound;
    private float volume = 1.0F;
    private float pitch = 1.0F;
    private SoundCategory category = SoundCategory.PLAYERS;
    private boolean onlyForPlayer = false;

    @JsonCreator
    protected ActionSound(@JacksonInject WolfyUtils wolfyUtils) {
        super(wolfyUtils, KEY, DataLocation.class);
    }

    @Override
    public void execute(WolfyUtils core, DataLocation data) {
        var location = data.getLocation();
        if (location == null || location.getWorld() == null) {
            return;
        }
        if (data instanceof DataPlayer dataPlayer && onlyForPlayer) {
            dataPlayer.getPlayer().playSound(location, sound, category, volume, pitch);
        } else {
            location.getWorld().playSound(location, sound, category, volume, pitch);
        }
    }

    public void setSound(Sound sound) {
        Preconditions.checkArgument(sound != null, "Sound cannot be null!");
        this.sound = sound;
    }

    public void setCategory(SoundCategory category) {
        this.category = category;
    }

    public void setOnlyForPlayer(boolean onlyForPlayer) {
        this.onlyForPlayer = onlyForPlayer;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }
}
