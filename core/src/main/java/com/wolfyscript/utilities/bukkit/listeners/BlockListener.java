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

package com.wolfyscript.utilities.bukkit.listeners;

import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.persistent.PersistentStorage;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.events.CustomItemBreakEvent;
import me.wolfyscript.utilities.util.events.CustomItemPlaceEvent;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import me.wolfyscript.utilities.util.world.BlockCustomItemStore;
import me.wolfyscript.utilities.util.world.WorldUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Bed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class BlockListener implements Listener {

    private WolfyCoreBukkit core;
    private PersistentStorage persistentStorage;

    public BlockListener(WolfyCoreBukkit core) {
        this.core = core;
        this.persistentStorage = this.core.getPersistentStorage();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        var block = event.getBlock();
        var worldStorage = persistentStorage.getOrCreateWorldStorage(block.getWorld());
        worldStorage.getBlock(block.getLocation()).ifPresent(store -> {
            var storedItem = store.getCustomItem();
            if (!ItemUtils.isAirOrNull(storedItem)) {
                event.setDropItems(false);
                var event1 = new CustomItemBreakEvent(storedItem, event);
                Bukkit.getPluginManager().callEvent(event1);
                event.setCancelled(event1.isCancelled());
                storedItem = event1.getCustomItem();
                if (!event1.isCancelled() && !ItemUtils.isAirOrNull(storedItem)) {
                    ItemStack result = dropItems(block, storedItem);
                    worldStorage.removeBlock(block.getLocation());
                    if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE) && event1.isDropItems()) {
                        block.getWorld().dropItemNaturally(block.getLocation(), result);
                    }
                    removeMultiBlockItems(block);
                }
            }
        });
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        dropItemsOnExplosion(event.isCancelled(), event.blockList());
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        dropItemsOnExplosion(event.isCancelled(), event.blockList());
    }

    private void dropItemsOnExplosion(boolean cancelled, List<Block> blocks) {
        if (!cancelled) {
            Iterator<Block> blockList = blocks.iterator();
            while (blockList.hasNext()) {
                var block = blockList.next();
                persistentStorage.getOrCreateWorldStorage(block.getWorld()).getBlock(block.getLocation()).ifPresent(store -> {
                    var storedItem = store.getCustomItem();
                    if (!ItemUtils.isAirOrNull(storedItem)) {
                        blockList.remove();
                        block.setType(Material.AIR);
                        block.getWorld().dropItemNaturally(block.getLocation(), dropItems(block, storedItem));
                        removeMultiBlockItems(block);
                    }
                });
            }
        }
    }

    private ItemStack dropItems(Block block, CustomItem storedItem) {
        ItemStack result = storedItem.create();
        if (block.getState() instanceof Container container) {
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
        return result;
    }

    private void removeMultiBlockItems(Block block) {
        var worldStorage = persistentStorage.getOrCreateWorldStorage(block.getWorld());
        if (block.getBlockData() instanceof Bisected bisected) {
            worldStorage.removeBlock(bisected.getHalf().equals(Bisected.Half.BOTTOM) ? block.getLocation().add(0, 1, 0) : block.getLocation().subtract(0, 1, 0));
        } else if (block.getBlockData() instanceof Bed bed) {
            worldStorage.removeBlock(block.getLocation().add(bed.getFacing().getDirection()));
        }
    }

    /*
    Called when liquid flows or when a Dragon Egg teleports.
    This Listener only listens for the Dragon Egg
    */
    @EventHandler(ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        var block = event.getBlock();
        var worldStore = persistentStorage.getOrCreateWorldStorage(event.getBlock().getWorld());
        worldStore.getBlock(block.getLocation()).ifPresent(store -> {
            var storedItem = store.getCustomItem();
            if (!ItemUtils.isAirOrNull(storedItem)) {
                worldStore.removeBlock(block.getLocation());
                worldStore.storeBlock(event.getToBlock().getLocation(), new BlockCustomItemStore(storedItem, null));
            }
        });
    }

    /**
     * Piston Events to make sure the position of CustomItems is updated correctly.
     *
     */
    @EventHandler(ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        updatePistonBlocks(event.getBlocks(), event.getDirection());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        updatePistonBlocks(event.getBlocks(), event.getDirection());
    }

    private void updatePistonBlocks(List<Block> blocks, BlockFace direction) {
        HashMap<Location, CustomItem> newLocations = new HashMap<>();
        blocks.forEach(block -> {
            var worldStorage = persistentStorage.getOrCreateWorldStorage(block.getWorld());
            worldStorage.getBlock(block.getLocation()).ifPresent(customItemStore -> {
                var storedItem = customItemStore.getCustomItem();
                if (storedItem != null) {
                    worldStorage.removeBlock(block.getLocation());
                    newLocations.put(block.getRelative(direction).getLocation(), storedItem);
                }
            });
        });
        newLocations.forEach((location, customItem) -> persistentStorage.getOrCreateWorldStorage(location.getWorld()).storeBlock(location, new BlockCustomItemStore(customItem, null)));
    }

    /*
     * Update the CustomItems if they disappear because of natural causes.
     */

    /*
    Unregisters the placed CustomItem when the block is burned by fire.
     */
    @EventHandler(ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        removeIfAvailable(event.getBlock());
    }

    /*
    Unregisters the placed CustomItem when the CustomItem is a leaf and decays.
     */
    @EventHandler(ignoreCancelled = true)
    public void onLeavesDecay(LeavesDecayEvent event) {
        removeIfAvailable(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
        if (event.getNewState().getType().equals(Material.AIR)) {
            removeIfAvailable(event.getBlock());
        }
    }

    private void removeIfAvailable(Block block) {
        var worldStorage = persistentStorage.getOrCreateWorldStorage(block.getWorld());
        worldStorage.getBlock(block.getLocation()).ifPresent(customItemStore -> {
            var storedItem = customItemStore.getCustomItem();
            if (storedItem != null) {
                worldStorage.removeBlock(block.getLocation());
            }
        });
    }

    @EventHandler
    public void test(EntityChangeBlockEvent event) {

    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (event.getChangedType().equals(Material.AIR)) {
            removeIfAvailable(event.getBlock());
        }
    }

    /**
     * Update the CustomItem when it is placed by a Player
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.canBuild()) {
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
                        persistentStorage.getOrCreateWorldStorage(event.getBlock().getWorld()).storeBlock(event.getBlockPlaced().getLocation(), new BlockCustomItemStore(customItem, null));
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlaceMulti(BlockMultiPlaceEvent event) {
        var worldStorage = persistentStorage.getOrCreateWorldStorage(event.getBlock().getWorld());
        var customItem = CustomItem.getByItemStack(event.getItemInHand());
        if (!ItemUtils.isAirOrNull(customItem)) {
            if (customItem.isBlockPlacement()) {
                event.setCancelled(true);
                return;
            }
            event.getReplacedBlockStates().forEach(state -> worldStorage.storeBlock(state.getLocation(), new BlockCustomItemStore(customItem, null)));
        }
    }
}
