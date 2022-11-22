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

package me.wolfyscript.utilities.api.inventory.custom_items.actions;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

public abstract class EventPlayerInteractEntityAbstract<T extends PlayerInteractEntityEvent> extends EventPlayer<me.wolfyscript.utilities.api.inventory.custom_items.actions.DataPlayerEvent<T>> {

    private List<EquipmentSlot> hand = List.of(EquipmentSlot.HAND);
    private List<EntityType> entityType = List.of();

    public EventPlayerInteractEntityAbstract(BukkitNamespacedKey key) {
        super(key, (Class<me.wolfyscript.utilities.api.inventory.custom_items.actions.DataPlayerEvent<T>>)(Object) me.wolfyscript.utilities.api.inventory.custom_items.actions.DataPlayerEvent.class);
    }

    @Override
    public void call(WolfyUtilCore core, DataPlayerEvent<T> data) {
        PlayerInteractEntityEvent event = data.getEvent();
        if ((hand.isEmpty() || hand.contains(event.getHand())) && (entityType.isEmpty() || entityType.contains(event.getRightClicked().getType()))) {
            super.call(core, data);
        }
    }
}
