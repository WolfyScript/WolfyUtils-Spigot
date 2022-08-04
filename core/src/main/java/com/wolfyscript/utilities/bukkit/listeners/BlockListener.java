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
import com.wolfyscript.utilities.bukkit.events.persistent.BlockStoreDropItemsEvent;
import com.wolfyscript.utilities.bukkit.items.CustomItemBlockData;
import com.wolfyscript.utilities.bukkit.persistent.PersistentStorage;
import com.wolfyscript.utilities.bukkit.persistent.world.BlockStorage;
import com.wolfyscript.utilities.bukkit.persistent.world.WorldStorage;
import java.util.Map;
import java.util.Objects;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.events.CustomItemPlaceEvent;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Bed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDropItemEvent;
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

import java.util.HashMap;
import java.util.List;
import org.bukkit.metadata.FixedMetadataValue;

public class BlockListener implements Listener {

    private static final String PREVIOUS_BROKEN_STORE = "previous_store";

    private final WolfyCoreBukkit core;
    private final PersistentStorage persistentStorage;

    public BlockListener(WolfyCoreBukkit core) {
        this.core = core;
        this.persistentStorage = this.core.getPersistentStorage();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        var block = event.getBlock();
        var worldStorage = persistentStorage.getOrCreateWorldStorage(block.getWorld());
        worldStorage.getBlock(block.getLocation()).ifPresent(store -> {
            if (!store.isEmpty()) {
                store.onBreak(event);
                if (event.isCancelled()) return; // Check if event is cancelled, as data might cancel it!
                worldStorage.removeBlock(block.getLocation()).ifPresent(storage -> {
                    if (event.isDropItems()) {
                        event.getBlock().setMetadata(PREVIOUS_BROKEN_STORE, new FixedMetadataValue(core, storage));
                    }
                });
            }
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        handleExplodedBlockStorages(persistentStorage.getOrCreateWorldStorage(event.getEntity().getWorld()), event.blockList());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockExplode(BlockExplodeEvent event) {
        var worldStorage = persistentStorage.getOrCreateWorldStorage(event.getBlock().getWorld());
        worldStorage.removeBlock(event.getBlock().getLocation()); // Remove the block that exploded, since that might have had custom data.
        handleExplodedBlockStorages(worldStorage, event.blockList());
    }

    private void handleExplodedBlockStorages(WorldStorage worldStorage, List<Block> blocks) {
        //Temporarily save the BlockStorage in the blocks metadata container, so the items are dropped correctly.
        for (Block block : blocks) {
            worldStorage.removeBlock(block.getLocation()).ifPresent(storage -> {
                if (!storage.isEmpty()) {
                    block.setMetadata(PREVIOUS_BROKEN_STORE, new FixedMetadataValue(core, storage));
                }
            });
        }
    }

    @EventHandler
    public void onBlockItemDrop(BlockDropItemEvent event) {
        var state = event.getBlockState(); // Get previous state with old metadata.
        state.getMetadata(PREVIOUS_BROKEN_STORE).stream().filter(metadataValue -> Objects.equals(metadataValue.getOwningPlugin(), core)).findFirst().ifPresent(metadataValue -> {
            event.getBlock().removeMetadata(PREVIOUS_BROKEN_STORE, core); //Remove old metadata from block!
            if (metadataValue.value() instanceof BlockStorage store) {
                var blockStoreDropItemsEvent = new BlockStoreDropItemsEvent(event.getPlayer(), event.getBlock(), store, state, event.getItems());
                Bukkit.getPluginManager().callEvent(blockStoreDropItemsEvent);
                event.setCancelled(blockStoreDropItemsEvent.isCancelled());
            }
        });
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
            Location loc = event.getToBlock().getLocation();
            worldStore.removeBlock(block.getLocation());
            store.copyToOtherBlockStorage(worldStore.getOrCreateBlockStorage(loc));
        });
    }

    /**
     * Piston Events to make sure the position of CustomItems is updated correctly.
     *
     */
    @EventHandler(ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        updatePistonBlocks(event.getBlock().getWorld(), event.getBlocks(), event.getDirection());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        updatePistonBlocks(event.getBlock().getWorld(), event.getBlocks(), event.getDirection());
    }

    private void updatePistonBlocks(World world, List<Block> blocks, BlockFace direction) {
        var worldStorage = persistentStorage.getOrCreateWorldStorage(world);
        blocks.forEach(block -> {
            worldStorage.getBlock(block.getLocation()).flatMap(store -> store.getData(CustomItemBlockData.ID, CustomItemBlockData.class)).ifPresent(data -> {
                var storedItem = data.getCustomItem();
                storedItem.ifPresent(customItem -> {
                    worldStorage.removeBlock(block.getLocation());
                    worldStorage.getOrCreateBlockStorage(block.getRelative(direction).getLocation()).addOrSetData(new CustomItemBlockData(core, customItem.getNamespacedKey()));
                });
            });
        });
    }

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

    /*
     * Update the CustomItems if they disappear because of natural causes.
     */
    @EventHandler(ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
        if (event.getNewState().getType().equals(Material.AIR)) {
            removeIfAvailable(event.getBlock());
        }
    }

    private void removeIfAvailable(Block block) {
        var worldStorage = persistentStorage.getOrCreateWorldStorage(block.getWorld());
        worldStorage.getBlock(block.getLocation()).ifPresent(customItemStore -> {
            worldStorage.removeBlock(block.getLocation());
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
                        var loc = event.getBlockPlaced().getLocation();
                        var storage = persistentStorage.getOrCreateWorldStorage(event.getBlock().getWorld()).getOrCreateBlockStorage(loc);
                        storage.addOrSetData(new CustomItemBlockData(core, customItem.getNamespacedKey()));
                        storage.onPlace(event);
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
            event.getReplacedBlockStates().forEach(state -> {
                worldStorage.getOrCreateBlockStorage(state.getLocation()).addOrSetData(new CustomItemBlockData(core, customItem.getNamespacedKey()));
            });
        }
    }
}
