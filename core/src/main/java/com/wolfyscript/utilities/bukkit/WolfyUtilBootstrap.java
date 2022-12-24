package com.wolfyscript.utilities.bukkit;

import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManagerBukkit;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import com.wolfyscript.utilities.bukkit.registry.BukkitRegistries;
import com.wolfyscript.utilities.versioning.ServerVersion;
import java.io.File;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
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
 * <ul>
 *     <li>{@link #getAPI(Plugin)} - Simple method to get your instance. Only use this in your <strong>onEnable()</strong></li>
 *     <li>{@link #getAPI(Plugin, boolean)} - Specify if it should init Event Listeners. Can be used inside the onLoad(), or plugin constructor, if set to false; Else only use this in your <strong>onEnable()</strong></li>
 *     <li>{@link #getAPI(Plugin, Class)} - Specify the type of your {@link CustomCache}. Can be used inside the onLoad(), or plugin constructor.</li>
 * </ul>
 * </p>
 */
public final class WolfyUtilBootstrap extends JavaPlugin {

    //Static reference to the instance of this class.
    private static WolfyUtilBootstrap instance;
    private final WolfyCoreBukkit core;
    private Metrics metrics;
    private BukkitAudiences adventure;
    private final Reflections reflections;

    WolfyUtilBootstrap() {
        super();
        ServerVersion.setWUVersion(getDescription().getVersion());
        this.core = new WolfyCoreBukkit(this);
        this.reflections = initReflections();
    }

    WolfyUtilBootstrap(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        this.core = new WolfyCoreBukkit(this);
        ServerVersion.setWUVersion(getDescription().getVersion());
        ServerVersion.setIsJUnitTest(true);
        System.setProperty("bstats.relocatecheck", "false");
        this.reflections = initReflections();
    }

    private Reflections initReflections() {
        return new Reflections(new ConfigurationBuilder().forPackages("me.wolfyscript", "com.wolfyscript").addClassLoaders(getClassLoader()).addScanners(Scanners.TypesAnnotated, Scanners.SubTypes, Scanners.Resources));
    }

    @Override
    public void onLoad() {
        getServer().getServicesManager().register(WolfyCoreBukkit.class, core, this, ServicePriority.Highest);

        core.load();
    }

    @Override
    public void onEnable() {
        this.adventure = BukkitAudiences.create(this);
        this.metrics = new Metrics(this, 5114);
        core.enable();
    }

    @Override
    public void onDisable() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        core.disable();
    }

    @NotNull
    public BukkitAudiences getAdventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    /**
     * Gets an instance of the core plugin.
     * <strong>Only use this if necessary! First try to get the instance via your {@link WolfyUtilsBukkit} instance!</strong>
     *
     * @return The instance of the core.
     */
    @Deprecated
    static WolfyUtilBootstrap getInstance() {
        return instance;
    }

    public WolfyCoreBukkit getCore() {
        return core;
    }

    public Reflections getReflections() {
        return reflections;
    }

}
