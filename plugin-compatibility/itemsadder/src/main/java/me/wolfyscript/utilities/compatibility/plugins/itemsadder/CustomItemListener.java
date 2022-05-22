package me.wolfyscript.utilities.compatibility.plugins.itemsadder;

import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.compatibility.plugins.ItemsAdderImpl;
import me.wolfyscript.utilities.util.events.CustomItemPlaceEvent;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.world.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

public class CustomItemListener implements Listener {

    private final ItemsAdderImpl iaImpl;

    public CustomItemListener(ItemsAdderImpl iaImpl) {
        this.iaImpl = iaImpl;
    }

    @EventHandler
    public void onBlockPlacement(CustomBlockPlaceEvent event) {
        if (event.isCanBuild()) {
            var customItem = CustomItem.getByItemStack(event.getItemInHand());
            if (!ItemUtils.isAirOrNull(customItem) && customItem.getItemStack().getType().isBlock()) {
                if (customItem.isBlockPlacement()) {
                    event.setCancelled(true);
                }
                var event1 = new CustomItemPlaceEvent(customItem, event.getBlock(), event.getReplacedBlockState(), event.getPlacedAgainst(), event.getItemInHand(), event.getPlayer(), event.isCanBuild(), EquipmentSlot.HAND);
                Bukkit.getPluginManager().callEvent(event1);
                customItem = event1.getCustomItem();

                if (!event1.isCancelled()) {
                    if (customItem != null) {
                        WorldUtils.getWorldCustomItemStore().store(event.getBlock().getLocation(), customItem);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(CustomBlockBreakEvent event) {
        var block = event.getBlock();
        var storedItem = WorldUtils.getWorldCustomItemStore().getCustomItem(block.getLocation());
        if (!ItemUtils.isAirOrNull(storedItem)) {
            //event.setDropItems(false);
            WorldUtils.getWorldCustomItemStore().remove(block.getLocation());
        }
    }

}
