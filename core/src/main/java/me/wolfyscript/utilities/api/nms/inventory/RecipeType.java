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

package me.wolfyscript.utilities.api.nms.inventory;

public enum RecipeType {

    CRAFTING("crafting"),
    SMELTING("smelting"),
    BLASTING("blasting"),
    SMOKING("smoking"),
    CAMPFIRE_COOKING("campfire_cooking"),
    STONECUTTING("stonecutting"),
    SMITHING("smithing");

    private final String id;

    RecipeType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
