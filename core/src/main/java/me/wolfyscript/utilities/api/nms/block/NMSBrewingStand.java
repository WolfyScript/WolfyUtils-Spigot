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

package me.wolfyscript.utilities.api.nms.block;

@Deprecated(forRemoval = true, since = "4.17")
public interface NMSBrewingStand {

    /**
     * @return The actual FuelLevel from the TileEntity
     */
    @Deprecated(forRemoval = true, since = "4.17")
    int getFuelLevel();

    /**
     * Sets the actual Fuel Level to the TileEntity.
     * This method directly changes the value other than Bukkit, which edits a copy of the TileEntity.
     *
     * @param fuelLevel The fuel level
     */
    @Deprecated(forRemoval = true, since = "4.17")
    void setFuelLevel(int fuelLevel);

    /**
     * @return The actual Brewing time from the TileEntity
     */
    @Deprecated(forRemoval = true, since = "4.17")
    int getBrewingTime();

    /**
     * Sets the actual Brewing time to the TileEntity.
     * This method directly changes the value other than Bukkit, which edits a copy of the TileEntity.
     *
     * @param brewTime The Brewing time in ticks
     */
    @Deprecated(forRemoval = true, since = "4.17")
    void setBrewingTime(int brewTime);
}
