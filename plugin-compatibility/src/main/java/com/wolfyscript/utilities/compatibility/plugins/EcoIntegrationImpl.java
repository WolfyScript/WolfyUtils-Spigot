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
import com.willfp.eco.core.items.CustomItem;
import com.willfp.eco.core.items.Items;
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.annotations.WUPluginIntegration;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.EcoIntegration;
import com.wolfyscript.utilities.bukkit.world.items.references.APIReference;
import com.wolfyscript.utilities.bukkit.compatibility.PluginIntegrationAbstract;
import com.wolfyscript.utilities.compatibility.plugins.eco.EcoRefImpl;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

@WUPluginIntegration(pluginName = EcoIntegration.KEY)
public class EcoIntegrationImpl extends PluginIntegrationAbstract implements EcoIntegration {

    /**
     * The main constructor that is called whenever the integration is created.<br>
     *
     * @param core       The WolfyUtilCore.
     */
    @Inject
    protected EcoIntegrationImpl(WolfyCoreBukkit core) {
        super(core, EcoIntegration.KEY);
    }

    @Override
    public void init(Plugin plugin) {
        core.registerAPIReference(new EcoRefImpl.Parser());
    }

    @Override
    public boolean isAPIReferenceIncluded(APIReference reference) {
        return reference instanceof EcoRefImpl;
    }

    @Override
    public boolean hasAsyncLoading() {
        return false;
    }

    @Override
    public boolean isCustomItem(ItemStack itemStack) {
        return Items.isCustomItem(itemStack);
    }

    @Override
    @Nullable
    public NamespacedKey getCustomItem(ItemStack itemStack) {
        CustomItem customItem = Items.getCustomItem(itemStack);
        return customItem != null ? customItem.getKey() : null;
    }

    @Override
    public ItemStack lookupItem(String key) {
        return Items.lookup(key).getItem();
    }


}