package com.wolfyscript.utilities.bukkit.listeners.custom_item;

import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.events.persistent.BlockStoreBreakEvent;
import com.wolfyscript.utilities.bukkit.events.persistent.BlockStoreDropItemsEvent;
import com.wolfyscript.utilities.bukkit.events.persistent.BlockStoreMultiPlaceEvent;
import com.wolfyscript.utilities.bukkit.events.persistent.BlockStorePlaceEvent;
import com.wolfyscript.utilities.bukkit.items.CustomItemBlockData;
import com.wolfyscript.utilities.bukkit.persistent.world.ChunkStorage;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.events.CustomItemPlaceEvent;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class CustomItemDataListener implements Listener {

    private final WolfyCoreBukkit core;

    public CustomItemDataListener(WolfyCoreBukkit core) {
        this.core = core;
    }

    @EventHandler
    public void onDropItems(BlockStoreDropItemsEvent event) {
        event.getStore().getData(CustomItemBlockData.ID, CustomItemBlockData.class).ifPresent(data -> {
            var blockState = event.getBlockState();
            data.getCustomItem().ifPresent(customItem -> {
                //TODO for future: Let people customize this!
                ItemStack result = customItem.create();
                if (blockState instanceof Container container) {
                    var blockStateMeta = (BlockStateMeta) result.getItemMeta();
                    if (container instanceof ShulkerBox) {
                        var shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
                        shulkerBox.getInventory().setContents(container.getInventory().getContents());
                        blockStateMeta.setBlockState(shulkerBox);
                    } else {
                        var itemContainer = (Container) blockStateMeta.getBlockState();
                        itemContainer.getInventory().clear();
                        blockStateMeta.setBlockState(itemContainer);
                    }
                    result.setItemMeta(blockStateMeta);
                }
                blockState.getWorld().dropItemNaturally(blockState.getLocation(), result);
            });
        });
    }

    @EventHandler
    public void onPlaceBlock(BlockStorePlaceEvent event) {
        var customItem = CustomItem.getByItemStack(event.getItemInHand());
        if (!ItemUtils.isAirOrNull(customItem) && customItem.getItemStack().getType().isBlock()) {
            if (customItem.isBlockPlacement()) {
                event.setCancelled(true);
            }
            var event1 = new CustomItemPlaceEvent(customItem, event);
            Bukkit.getPluginManager().callEvent(event1);
            customItem = event1.getCustomItem();
            if (!event1.isCancelled()) {
                if (customItem != null) {
                    Location blockLoc = event.getBlockPlaced().getLocation();
                    ChunkStorage chunkStorage = core.getPersistentStorage().getOrCreateWorldStorage(blockLoc.getWorld()).getOrCreateChunkStorage(blockLoc);
                    var customItemData = new CustomItemBlockData(core, chunkStorage, blockLoc.toVector(), customItem.getNamespacedKey());
                    event.getStore().addOrSetData(customItemData);
                    customItemData.onPlace(event);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMultiPlaceBlock(BlockStoreMultiPlaceEvent event) {
        var customItem = CustomItem.getByItemStack(event.getItemInHand());
        if (!ItemUtils.isAirOrNull(customItem)) {
            if (customItem.isBlockPlacement()) {
                event.setCancelled(true);
                return;
            }
            event.getBlockStorages().forEach(blockStorage -> {
                Location blockLoc = event.getBlockPlaced().getLocation();
                ChunkStorage chunkStorage = core.getPersistentStorage().getOrCreateWorldStorage(blockLoc.getWorld()).getOrCreateChunkStorage(blockLoc);
                var customItemData = new CustomItemBlockData(core, chunkStorage, blockLoc.toVector(), customItem.getNamespacedKey());
                blockStorage.addOrSetData(customItemData);
                customItemData.onPlace(event);
            });
        }
    }

    @EventHandler
    public void onBreakBlock(BlockStoreBreakEvent event) {
        event.getStore().getData(CustomItemBlockData.ID, CustomItemBlockData.class).ifPresent(data -> {
            data.onBreak(event);
        });
    }


}
