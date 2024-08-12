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

package me.wolfyscript.utilities.api.nms;

import com.wolfyscript.utilities.bukkit.nms.fallback.FallbackNMSEntry;
import java.util.ArrayList;
import java.util.List;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.Reflection;
import me.wolfyscript.utilities.util.version.MinecraftVersion;
import me.wolfyscript.utilities.util.version.ServerVersion;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public abstract class NMSUtil {

    private static final List<VersionHandler> versionHandlers = new ArrayList<>();
    private static final HashMap<String, VersionHandler> versionAdapters = new HashMap<>();

    static {
        registerAdapter(new VersionAdapter("v1_17_R1"));
        registerAdapter(new VersionAdapter("v1_18_R1"));

        registerAdapter(new MinecraftToNMSRevision("v1_20_R1", "1.20.0", "1.20.1"));
        registerAdapter(new MinecraftToNMSRevision("v1_20_R2", "1.20.2"));
        registerAdapter(new MinecraftToNMSRevision("v1_20_R3", "1.20.3", "1.20.4"));
        registerAdapter(new MinecraftToNMSRevision("v1_20_R4", "1.20.5", "1.20.6"));
    }

    private static void registerAdapter(VersionHandler adapter) {
        for (String minecraftVersion : adapter.getMinecraftVersions()) {
            versionAdapters.put(minecraftVersion, adapter);
        }
    }

    private final WolfyUtilities wolfyUtilities;

    private final Plugin plugin;
    protected BlockUtil blockUtil;
    protected ItemUtil itemUtil;
    protected RecipeUtil recipeUtil;
    protected InventoryUtil inventoryUtil;
    protected NBTUtil nbtUtil;

    protected NetworkUtil networkUtil;

    /**
     * The class that implements this NMSUtil needs to have a constructor with just the WolfyUtilities parameter.
     *
     * @param wolfyUtilities
     */
    protected NMSUtil(WolfyUtilities wolfyUtilities) {
        this.wolfyUtilities = wolfyUtilities;
        this.plugin = wolfyUtilities.getPlugin();
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    /**
     * Creates an instance of the specific NMSUtil of the current Minecraft version.
     *
     * @param wolfyUtilities
     * @return
     */
    public static NMSUtil create(WolfyUtilities wolfyUtilities) {
        if(ServerVersion.isIsJUnitTest()) {
            return null;
        }
        if (ServerVersion.getVersion().isBefore(MinecraftVersion.of(1, 21, 0))) {
            return new FallbackNMSEntry(wolfyUtilities); // When using 1.21+ WolfyUtils no longer provides NMSUtils
        }
        String version = Reflection.getVersion().orElse(null);
        try {
            final VersionHandler adapter;
            if (version == null) {
                // Using mojang mappings
                adapter = versionAdapters.get(ServerVersion.getVersion().getVersion());
            } else {
                // Use Spigot mappings
                adapter = versionAdapters.get(version);
            }
            if (adapter != null) {
                version = adapter.getPackageName();
            }
            wolfyUtilities.getConsole().getLogger().info("NMS Version: " + version);
            String className = NMSUtil.class.getPackage().getName() + '.' + version + ".NMSEntry";
            Class<?> nmsUtilsType = Class.forName(className);
            if (NMSUtil.class.isAssignableFrom(nmsUtilsType)) {
                Constructor<?> constructor = nmsUtilsType.getDeclaredConstructor(WolfyUtilities.class);
                constructor.setAccessible(true);
                return ((NMSUtil) constructor.newInstance(wolfyUtilities));
            }
        } catch (ReflectiveOperationException ex) {
            // failed to find NMS implementation
        }
        //Unsupported version
        wolfyUtilities.getCore().getLogger().warning("Did not detect NMS implementation for server version (" + version + ")! Using Fallback! This might cause issue if plugins use the NMS Utils!");
        return new FallbackNMSEntry(wolfyUtilities);
    }

    public WolfyUtilities getWolfyUtilities() {
        return wolfyUtilities;
    }

    @Deprecated(forRemoval = true, since = "4.17")
    public BlockUtil getBlockUtil() {
        return blockUtil;
    }

    @Deprecated(forRemoval = true, since = "4.17")
    public ItemUtil getItemUtil() {
        return itemUtil;
    }

    @Deprecated(forRemoval = true, since = "4.17")
    public InventoryUtil getInventoryUtil() {
        return inventoryUtil;
    }

    @Deprecated(forRemoval = true, since = "4.17")
    public NBTUtil getNBTUtil() {
        return nbtUtil;
    }

    @Deprecated(forRemoval = true, since = "4.17")
    public RecipeUtil getRecipeUtil() {
        return recipeUtil;
    }

    @Deprecated(forRemoval = true, since = "4.17")
    public NetworkUtil getNetworkUtil() {
        return networkUtil;
    }

    /**
     * Used to handle new types of NMS versions introduced with spigot 1.17 thanks to the use of Mojang mappings.
     */
    private static class VersionAdapter implements VersionHandler {

        protected final String version;

        public VersionAdapter(String entryVersion) {
            this.version = entryVersion;
        }

        @Override
        public String getPackageName() {
            if (ServerVersion.getVersion().getPatch() > 0) {
                return version + "_P" + ServerVersion.getVersion().getPatch();
            }
            return version;
        }

        @Override
        public String[] getMinecraftVersions() {
            return new String[]{version};
        }
    }

    private static class MinecraftToNMSRevision implements VersionHandler {

        private final String[] mcVersions;
        private final String nmsRevision;

        private MinecraftToNMSRevision(String nmsRevision, String... mcVersions) {
            this.mcVersions = mcVersions;
            this.nmsRevision = nmsRevision;
        }

        @Override
        public String getPackageName() {
            return nmsRevision;
        }

        @Override
        public String[] getMinecraftVersions() {
            return mcVersions;
        }
    }

    private interface VersionHandler {

        String getPackageName();

        String[] getMinecraftVersions();

    }

}
