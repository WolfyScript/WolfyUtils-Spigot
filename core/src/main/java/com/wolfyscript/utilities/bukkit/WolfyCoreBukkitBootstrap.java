package com.wolfyscript.utilities.bukkit;

import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManagerBukkit;
import com.wolfyscript.utilities.bukkit.registry.BukkitRegistries;
import com.wolfyscript.utilities.versioning.ServerVersion;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

/**
 * This abstract class is the actual core of the plugin (This class is being extended by the plugin instance).<br>
 * <p>
 * It provides access to internal functionality like {@link BukkitRegistries}, {@link CompatibilityManagerBukkit}, and of course the creation of the API instance.<br>
 * <p>
 * To get an instance of the API ({@link WolfyUtilsBukkit}) for your plugin you need one of the following methods. <br>
 * </p>
 */
public final class WolfyCoreBukkitBootstrap extends WolfyCoreBootstrap {

    private final WolfyCoreBukkit core;

    public WolfyCoreBukkitBootstrap() {
        super();
        this.core = new WolfyCoreBukkit(this);
    }

    public WolfyCoreBukkit getCore() {
        return core;
    }

}
