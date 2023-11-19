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

package com.wolfyscript.utilities.compatibility.plugins;

import com.google.inject.Inject;
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.WolfyCoreImpl;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.MythicMobsIntegration;
import com.wolfyscript.utilities.compatibility.plugins.mythicmobs.MythicMobs5RefImpl;
import com.wolfyscript.utilities.compatibility.plugins.mythicmobs.MythicMobsRefImpl;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import com.wolfyscript.utilities.bukkit.annotations.WUPluginIntegration;
import com.wolfyscript.utilities.bukkit.world.items.references.APIReference;
import com.wolfyscript.utilities.bukkit.compatibility.PluginIntegrationAbstract;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

@WUPluginIntegration(pluginName = MythicMobsIntegration.KEY)
public class MythicMobsImpl extends PluginIntegrationAbstract implements MythicMobsIntegration {

    @Inject
    protected MythicMobsImpl(WolfyCoreBukkit core) {
        super(core, MythicMobsIntegration.KEY);
    }

    @Override
    public void init(Plugin plugin) {
        if (WolfyCoreImpl.hasClass("io.lumine.mythic.bukkit.MythicBukkit")) {
            core.registerAPIReference(new MythicMobs5RefImpl.Parser());
        } else {
            core.registerAPIReference(new MythicMobsRefImpl.Parser());
        }
        core.getRegistries().getStackIdentifierParsers().register(new com.wolfyscript.utilities.compatibility.plugins.mythicmobs.MythicMobsStackIdentifier.Parser());
    }

    @Override
    public boolean hasAsyncLoading() {
        return false;
    }

    @Override
    public boolean isAPIReferenceIncluded(APIReference reference) {
        return reference instanceof MythicMobsRefImpl;
    }

    @Override
    public void spawnMob(String mobName, Location location, int mobLevel) {
        MythicMob mythicMob = MythicMobs.inst().getMobManager().getMythicMob(mobName);
        if(mythicMob != null) {
            mythicMob.spawn(BukkitAdapter.adapt(location), mobLevel);
        }
    }
}
