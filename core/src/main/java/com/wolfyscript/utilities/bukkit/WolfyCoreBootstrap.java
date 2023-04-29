package com.wolfyscript.utilities.bukkit;

import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManagerBukkit;
import com.wolfyscript.utilities.bukkit.registry.BukkitRegistries;
import com.wolfyscript.utilities.common.WolfyCore;
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
public abstract class WolfyCoreBootstrap extends JavaPlugin {

    //Static reference to the instance of this class.
    private static WolfyCoreBootstrap instance;
    private Metrics metrics;
    private final Reflections reflections;

    public WolfyCoreBootstrap() {
        super();
        ServerVersion.setWUVersion(getDescription().getVersion());
        this.reflections = initReflections();
        instance = this;
    }

    private Reflections initReflections() {
        return new Reflections(new ConfigurationBuilder().forPackages("me.wolfyscript", "com.wolfyscript").addClassLoaders(getClassLoader()).addScanners(Scanners.TypesAnnotated, Scanners.SubTypes, Scanners.Resources));
    }

    @Override
    public void onLoad() {
        getServer().getServicesManager().register(WolfyCore.class, getCore(), this, ServicePriority.Highest);
        getCore().load();
    }

    @Override
    public void onEnable() {
        this.metrics = new Metrics(this, 5114);
        getCore().enable();
    }

    @Override
    public void onDisable() {
        getCore().disable();
    }

    /**
     * Gets an instance of the core plugin.
     * <strong>Only use this if necessary! First try to get the instance via your {@link WolfyUtilsBukkit} instance!</strong>
     *
     * @return The instance of the core.
     */
    @Deprecated
    static WolfyCoreBootstrap getInstance() {
        return instance;
    }

    public abstract WolfyCoreImpl getCore();

    public Reflections getReflections() {
        return reflections;
    }

}
