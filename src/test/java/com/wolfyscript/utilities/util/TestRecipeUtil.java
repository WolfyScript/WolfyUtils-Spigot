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

package com.wolfyscript.utilities.util;

import com.wolfyscript.utilities.world.items.crafting.RecipeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestRecipeUtil {

    @Test
    public void checkShapeFormat() {
        Assertions.assertIterableEquals(List.of("A ", " A", " A"), RecipeUtil.formatShape(" A ","  A", "  A"));
        Assertions.assertIterableEquals(List.of(" A ", "A A", "  A"), RecipeUtil.formatShape(" A ","A A", "  A"));
        Assertions.assertIterableEquals(List.of(" A ", "A A"), RecipeUtil.formatShape(" A ","A A", "   "));
        Assertions.assertIterableEquals(List.of("A ", " A"), RecipeUtil.formatShape(" A ","  A", "   "));


    }

}
