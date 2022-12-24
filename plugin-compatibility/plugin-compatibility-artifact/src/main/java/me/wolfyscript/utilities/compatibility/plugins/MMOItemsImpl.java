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

package me.wolfyscript.utilities.compatibility.plugins;

import com.wolfyscript.utilities.bukkit.annotations.WUPluginIntegration;
import com.wolfyscript.utilities.bukkit.WolfyUtilBootstrap;
import com.wolfyscript.utilities.bukkit.world.items.references.APIReference;
import com.wolfyscript.utilities.bukkit.compatibility.PluginIntegrationAbstract;
import me.wolfyscript.utilities.compatibility.plugins.mmoitems.MMOItemsRefImpl;
import org.bukkit.plugin.Plugin;

@WUPluginIntegration(pluginName = MMOItemsImpl.PLUGIN_NAME)
public class MMOItemsImpl extends PluginIntegrationAbstract {

    static final String PLUGIN_NAME = "MMOItems";

    protected MMOItemsImpl(WolfyUtilBootstrap core) {
        super(core, PLUGIN_NAME);
    }

    @Override
    public void init(Plugin plugin) {
        core.registerAPIReference(new MMOItemsRefImpl.Parser());
    }

    @Override
    public boolean hasAsyncLoading() {
        return false;
    }

    @Override
    public boolean isAPIReferenceIncluded(APIReference reference) {
        return reference instanceof MMOItemsRefImpl;
    }
}
