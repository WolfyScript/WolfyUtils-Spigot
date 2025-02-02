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

package me.wolfyscript.utilities.compatibility.plugins.itemsadder;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import me.wolfyscript.utilities.annotations.WUPluginIntegration;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.compatibility.PluginIntegrationAbstract;
import me.wolfyscript.utilities.compatibility.plugins.ItemsAdderIntegration;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@WUPluginIntegration(pluginName = ItemsAdderIntegration.KEY)
public class ItemsAdderImpl extends PluginIntegrationAbstract implements ItemsAdderIntegration, Listener {

    protected ItemsAdderImpl(WolfyUtilCore core) {
        super(core, ItemsAdderIntegration.KEY);
    }

    @Override
    public void init(Plugin plugin) {
        core.registerAPIReference(new ItemsAdderRefImpl.Parser());
        core.getRegistries().getStackIdentifierParsers().register(new ItemsAdderStackIdentifier.Parser());
        core.getRegistries().getStackIdentifierTypeRegistry().register(ItemsAdderStackIdentifier.class);
        Bukkit.getPluginManager().registerEvents(this, core);
        Bukkit.getPluginManager().registerEvents(new CustomItemListener(this), core);
    }

    @Override
    public boolean hasAsyncLoading() {
        return true;
    }

    @Override
    public boolean isAPIReferenceIncluded(APIReference reference) {
        return reference instanceof ItemsAdderRefImpl;
    }

    @EventHandler
    public void onLoaded(ItemsAdderLoadDataEvent event) {
        if (event.getCause().equals(ItemsAdderLoadDataEvent.Cause.FIRST_LOAD)) {
            markAsDoneLoading();
        }
    }

    @Override
    public Optional<CustomStack> getStackByItemStack(ItemStack itemStack) {
        return CustomStackWrapper.wrapStack(dev.lone.itemsadder.api.CustomStack.byItemStack(itemStack));
    }

    @Override
    public Optional<CustomStack> getStackInstance(String namespacedID) {
        return CustomStackWrapper.wrapStack(dev.lone.itemsadder.api.CustomStack.getInstance(namespacedID));
    }

    @Override
    public @Nullable Optional<CustomBlock> getBlockByItemStack(ItemStack itemStack) {
        return CustomBlockWrapper.wrapBlock(dev.lone.itemsadder.api.CustomBlock.byItemStack(itemStack));
    }

    @Override
    public @Nullable Optional<CustomBlock> getBlockPlaced(Block block) {
        return CustomBlockWrapper.wrapBlock(dev.lone.itemsadder.api.CustomBlock.byAlreadyPlaced(block));
    }

    @Override
    public @Nullable Optional<CustomBlock> getBlockInstance(String namespacedID) {
        return CustomBlockWrapper.wrapBlock(dev.lone.itemsadder.api.CustomBlock.getInstance(namespacedID));
    }
}
