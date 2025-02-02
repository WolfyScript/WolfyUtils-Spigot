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

package com.wolfyscript.utilities.bukkit.listeners.custom_item;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.*;
import me.wolfyscript.utilities.registry.RegistryCustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class CustomItemPlayerListener implements Listener {

    private final WolfyUtilCore core;
    private final RegistryCustomItem customItems;

    public CustomItemPlayerListener(WolfyUtilCore core) {
        this.core = core;
        this.customItems = core.getRegistries().getCustomItems();
    }

    private <T extends PlayerEvent> void callEvent(CustomItem item, NamespacedKey eventKey, T bukkitEvent) {
        item.getActionSettings().callEvent(eventKey, new DataPlayerEvent<>(bukkitEvent, bukkitEvent.getPlayer(), item));
    }

    @EventHandler
    private void onClick(PlayerInteractEvent event) {
        var item = customItems.getByItemStack(event.getItem());
        item.ifPresent(customItem -> callEvent(customItem, EventPlayerInteract.KEY, event));
    }

    @EventHandler
    private void onConsume(PlayerItemConsumeEvent event) {
        var item = customItems.getByItemStack(event.getItem());
        item.ifPresent(customItem -> callEvent(customItem, EventPlayerConsumeItem.KEY, event));
    }

    @EventHandler
    private void onInteractEntity(PlayerInteractEntityEvent event) {
        var item = customItems.getByItemStack(event.getPlayer().getEquipment().getItem(event.getHand()));
        item.ifPresent(customItem -> callEvent(customItem, EventPlayerInteractEntity.KEY, event));
    }

    @EventHandler
    private void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        var item = customItems.getByItemStack(event.getPlayer().getEquipment().getItem(event.getHand()));
        item.ifPresent(customItem -> callEvent(customItem, EventPlayerInteractAtEntity.KEY, event));
    }

    @EventHandler
    private void onItemBreak(PlayerItemBreakEvent event) {
        var item = customItems.getByItemStack(event.getBrokenItem());
        item.ifPresent(customItem -> callEvent(customItem, EventPlayerItemBreak.KEY, event));
    }

    @EventHandler
    private void onItemDamage(PlayerItemDamageEvent event) {
        var item = customItems.getByItemStack(event.getItem());
        item.ifPresent(customItem -> callEvent(customItem, EventPlayerItemDamage.KEY, event));
    }

    @EventHandler
    private void onItemDrop(PlayerDropItemEvent event) {
        var item = customItems.getByItemStack(event.getItemDrop().getItemStack());
        item.ifPresent(customItem -> callEvent(customItem, EventPlayerItemDrop.KEY, event));
    }

    @EventHandler
    private void onItemHandSwap(PlayerSwapHandItemsEvent event) {
        var item = customItems.getByItemStack(event.getMainHandItem());
        if (item.isPresent()) {
            callEvent(item.get(), EventPlayerItemHandSwap.KEY, event);
            return;
        }
        item = customItems.getByItemStack(event.getOffHandItem());
        item.ifPresent(customItem -> callEvent(customItem, EventPlayerItemHandSwap.KEY, event));
    }

    @EventHandler
    private void onItemHeld(PlayerItemHeldEvent event) {
        //TODO: Handle the item from the previous and new slot
    }

}
