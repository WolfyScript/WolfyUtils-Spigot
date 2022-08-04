package com.wolfyscript.utilities.bukkit.listeners.custom_item;

import com.wolfyscript.utilities.bukkit.events.persistent.BlockStoreDropItemsEvent;
import com.wolfyscript.utilities.bukkit.items.CustomItemBlockData;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class CustomItemDataListener implements Listener {

    private WolfyUtilCore core;

    public CustomItemDataListener(WolfyUtilCore core) {
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


}
