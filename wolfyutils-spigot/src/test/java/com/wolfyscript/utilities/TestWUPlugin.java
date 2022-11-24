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

package com.wolfyscript.utilities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.WolfyUtilCore;
import com.wolfyscript.utilities.bukkit.registry.BukkitRegistries;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TestWUPlugin {

    private static ServerMock server;
    private static WolfyCoreBukkit plugin;

    @BeforeAll
    public static void load() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(WolfyCoreBukkit.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Check plugin instance")
    public void verifyInstance() {
        Assertions.assertNotNull(WolfyUtilCore.getInstance());
        Assertions.assertNotNull(plugin.getRegistries());
        Assertions.assertNotNull(plugin.getCompatibilityManager());
        Assertions.assertNotNull(plugin.getWolfyUtils());
    }

    @Test
    public void checkRegistries() {
        BukkitRegistries registries = plugin.getRegistries();
        var customItems = registries.getCustomItems();
        Assertions.assertNotNull(customItems);
        var customItemData = registries.getCustomItemData();
        Assertions.assertNotNull(customItemData);
        var customItemNBTChecks = registries.getCustomItemNbtChecks();
        Assertions.assertNotNull(customItemNBTChecks);
        var particleTimers = registries.getParticleTimer();
        Assertions.assertNotNull(particleTimers);
        var particleAnimators = registries.getParticleAnimators();
        Assertions.assertNotNull(particleAnimators);
        var particleShapes = registries.getParticleShapes();
        Assertions.assertNotNull(particleShapes);
        var particleEffects = registries.getParticleEffects();
        Assertions.assertNotNull(particleEffects);
        var particleAnimations = registries.getParticleAnimations();
        Assertions.assertNotNull(particleAnimations);


    }

}
