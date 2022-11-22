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

package com.wolfyscript.utilities.bukkit.items.actions;

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilCore;
import java.util.List;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public abstract class EventPlayerInteractEntityAbstract<T extends PlayerInteractEntityEvent> extends EventPlayer<com.wolfyscript.utilities.bukkit.items.actions.DataPlayerEvent<T>> {

    private final List<EquipmentSlot> hand = List.of(EquipmentSlot.HAND);
    private final List<EntityType> entityType = List.of();

    public EventPlayerInteractEntityAbstract(BukkitNamespacedKey key) {
        super(key, (Class<com.wolfyscript.utilities.bukkit.items.actions.DataPlayerEvent<T>>)(Object) com.wolfyscript.utilities.bukkit.items.actions.DataPlayerEvent.class);
    }

    @Override
    public void call(WolfyUtilCore core, DataPlayerEvent<T> data) {
        PlayerInteractEntityEvent event = data.getEvent();
        if ((hand.isEmpty() || hand.contains(event.getHand())) && (entityType.isEmpty() || entityType.contains(event.getRightClicked().getType()))) {
            super.call(core, data);
        }
    }
}
