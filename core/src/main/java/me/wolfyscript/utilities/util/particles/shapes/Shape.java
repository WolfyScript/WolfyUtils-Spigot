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

package me.wolfyscript.utilities.util.particles.shapes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.json.KeyedTypeIdResolver;
import com.wolfyscript.utilities.json.KeyedTypeResolver;
import java.util.function.Consumer;
import me.wolfyscript.utilities.util.particles.timer.Timer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "key")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = {"key"})
public abstract class Shape implements Keyed {

    private final BukkitNamespacedKey key;

    protected Shape(@NotNull BukkitNamespacedKey key) {
        Preconditions.checkArgument(key != null && !key.getKey().isEmpty() && !key.getNamespace().isEmpty(), "Invalid NamespacedKey! Namespaced cannot be null or empty!");
        this.key = key;
    }

    /**
     * Applies the {@link Consumer<Vector>} for all vertices of the shape.<br>
     * Resource intensive tasks should be done beforehand, as this method might be called each tick.<br>
     * The consumer might be nested like in {@link ShapeComplexRotation} to rotate all vertices.<br>
     * Because of that the vertices should be copied, so changes won't get reflected to this shape vertices (If they were cached)!
     *
     * @param time The current time value from the timer. See {@link Timer.Runner#increase()}.
     * @param drawVector The consumer that calculates the vector and spawns the particles.
     */
    public abstract void drawVectors(double time, Consumer<Vector> drawVector);

    @JsonIgnore
    @Override
    public BukkitNamespacedKey getNamespacedKey() {
        return key;
    }

    public enum Direction {
        X_AXIS, Y_AXIS, Z_AXIS
    }
}
