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

package com.wolfyscript.utilities.bukkit.world;

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.WolfyUtilCore;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.io.BukkitObjectInputStream;

/**
 * Replaced by the {@link com.wolfyscript.utilities.bukkit.persistent.PersistentStorage}, that is
 * available using {@link WolfyCoreBukkit#getPersistentStorage()}.
 * @see com.wolfyscript.utilities.bukkit.persistent.PersistentStorage
 */
@Deprecated
public class WorldUtils {

    private WorldUtils() {
    }

    private static WorldCustomItemStore worldCustomItemStore;

    public static WorldCustomItemStore getWorldCustomItemStore() {
        return worldCustomItemStore;
    }

    /**
     * Loads the store from the file.
     */
    @Deprecated
    public static void load() {
        WolfyCoreBukkit.getInstance().getLogger().info("Loading stored Custom Items");
        var file = new File(WolfyCoreBukkit.getInstance().getWolfyUtils().getDataFolder() + File.separator + "world_custom_item.store");
        if (file.exists()) {
            try (var fin = new FileInputStream(file); var gzip = new GZIPInputStream(fin)) {
                worldCustomItemStore = JacksonUtil.getObjectMapper().readValue(gzip, WorldCustomItemStore.class);
                if (worldCustomItemStore == null) {
                    WolfyCoreBukkit.getInstance().getLogger().severe("Couldn't load stored CustomItems! Resetting to default!");
                    worldCustomItemStore = new WorldCustomItemStore();
                }
            } catch (IOException e) {
                WolfyCoreBukkit.getInstance().getLogger().severe("Couldn't load stored CustomItems! Resetting to default!");
                worldCustomItemStore = new WorldCustomItemStore();
            } finally {
                // Delete the file as we no longer need it!
                try {
                    Files.delete(file.toPath());
                } catch (IOException ex) {
                    WolfyUtilCore.getInstance().getLogger().severe("Could not delete the `world_custom_item.store`, trying to rename it.");
                    ex.printStackTrace();
                    if (file.renameTo(new File(WolfyCoreBukkit.getInstance().getWolfyUtils().getDataFolder() + File.separator + "world_custom_item.old.store"))) {
                        WolfyUtilCore.getInstance().getLogger().severe("Could not rename `world_custom_item.store`! Please delete manually!");
                    }
                }
            }
        } else {
            //Load from the old file if the new one doesn't exist!
            loadOld();
        }
    }

    @Deprecated
    private static void loadOld() {
        var file = new File(WolfyCoreBukkit.getInstance().getWolfyUtils().getDataFolder() + File.separator + "stored_block_items.dat");
        worldCustomItemStore = new WorldCustomItemStore();
        if (file.exists()) {
            try (var fis = new FileInputStream(file); BukkitObjectInputStream ois = new BukkitObjectInputStream(fis)) {
                var object = ois.readObject();
                HashMap<String, String> loadMap = (HashMap<String, String>) object;
                loadMap.forEach((key, value) -> {
                    var location = stringToLocation(key);
                    if (location != null) {
                        worldCustomItemStore.setStore(location, new BlockCustomItemStore(BukkitNamespacedKey.of(value), null));
                    }
                });
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                // Delete the file as we no longer need it!
                try {
                    Files.delete(file.toPath());
                } catch (IOException ex) {
                    WolfyUtilCore.getInstance().getLogger().severe("Could not delete the `stored_block_items.dat`, trying to rename it.");
                    ex.printStackTrace();
                    if (file.renameTo(new File(WolfyCoreBukkit.getInstance().getWolfyUtils().getDataFolder() + File.separator + "stored_block_items.old.dat"))) {
                        WolfyUtilCore.getInstance().getLogger().severe("Could not rename `stored_block_items.dat`! Please delete manually!");
                    }
                }
            }
        }
    }

    @Deprecated
    private static Location stringToLocation(String loc) {
        String[] args = loc.split(";");
        try {
            var uuid = UUID.fromString(args[0]);
            var world = Bukkit.getWorld(uuid);
            if (world != null) {
                return new Location(world, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            }
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Couldn't find world " + args[0]);
        }
        return null;
    }

}
