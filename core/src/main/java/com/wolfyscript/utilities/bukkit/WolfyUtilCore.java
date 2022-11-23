package com.wolfyscript.utilities.bukkit;

import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManager;
import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManagerBukkit;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import com.wolfyscript.utilities.bukkit.world.items.CustomItem;
import com.wolfyscript.utilities.bukkit.world.items.references.APIReference;
import com.wolfyscript.utilities.bukkit.registry.BukkitRegistries;
import com.wolfyscript.utilities.common.WolfyCore;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.wolfyscript.utilities.util.version.ServerVersion;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
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
public abstract class WolfyUtilCore extends JavaPlugin implements WolfyCore {

    //Static reference to the instance of this class.
    private static WolfyUtilCore instance;

    protected Reflections reflections;
    protected final Map<String, WolfyUtilsBukkit> wolfyUtilsInstances = new HashMap<>();
    protected final WolfyUtilsBukkit api;
    protected final BukkitRegistries registries;

    protected WolfyUtilCore() {
        super();
        if (instance == null && this.getName().equals("WolfyUtilities") && getClass().getPackageName().equals("com.wolfyscript.utilities.bukkit")) {
            instance = this;
        } else {
            throw new IllegalArgumentException("This constructor can only be called by WolfyUtilities itself!");
        }
        this.api = getAPI(this);
        ServerVersion.setWUVersion(getDescription().getVersion());
        this.registries = new BukkitRegistries(this);
        this.reflections = initReflections();
    }

    protected WolfyUtilCore(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        if (instance == null && this.getName().equals("WolfyUtilities") && getClass().getPackageName().equals("com.wolfyscript.utilities.bukkit")) {
            instance = this;
        } else {
            throw new IllegalArgumentException("This constructor can only be called by WolfyUtilities itself!");
        }
        ServerVersion.setWUVersion(getDescription().getVersion());
        ServerVersion.setIsJUnitTest(true);
        System.setProperty("bstats.relocatecheck", "false");
        this.api = getAPI(this);
        this.registries = new BukkitRegistries(this);
        this.reflections = initReflections();
    }

    private Reflections initReflections() {
        return new Reflections(new ConfigurationBuilder().forPackages("me.wolfyscript", "com.wolfyscript").addClassLoaders(getClassLoader()).addScanners(Scanners.TypesAnnotated, Scanners.SubTypes, Scanners.Resources));
    }

    /**
     * Gets an instance of the core plugin.
     * <strong>Only use this if necessary! First try to get the instance via your {@link WolfyUtilsBukkit} instance!</strong>
     *
     * @return The instance of the core.
     */
    @Deprecated
    public static WolfyUtilCore getInstance() {
        return instance;
    }

    @Override
    public WolfyUtilsBukkit getWolfyUtils() {
        return api;
    }

    /**
     * Gets the {@link BukkitRegistries} object, that contains all info about available registries.
     *
     * @return The {@link BukkitRegistries} object, to access registries.
     */
    public BukkitRegistries getRegistries() {
        return registries;
    }

    /**
     * Gets the {@link CompatibilityManagerBukkit}, that manages the plugins compatibility features.
     *
     * @return The {@link CompatibilityManagerBukkit}.
     */
    public abstract CompatibilityManager getCompatibilityManager();

    public abstract BukkitAudiences getAdventure();

    /**
     * Gets the {@link Reflections} instance of the plugins' package.
     *
     * @return The Reflection of the plugins' package.
     */
    public Reflections getReflections() {
        return reflections;
    }

    /**
     * Gets or create the {@link WolfyUtilsBukkit} instance for the specified plugin.
     *
     * @param plugin The plugin to get the instance for.
     * @return The WolfyUtilities instance for the plugin.
     */
    public WolfyUtilsBukkit getAPI(Plugin plugin) {
        return getAPI(plugin, false);
    }

    /**
     * Gets or create the {@link WolfyUtilsBukkit} instance for the specified plugin.<br>
     * In case init is enabled it will directly initialize the event listeners and possibly other things.<br>
     * <b>In case you disable init you need to run {@link WolfyUtilsBukkit#initialize()} inside your onEnable()!</b>
     *
     * @param plugin The plugin to get the instance for.
     * @param init   If it should directly initialize the APIs' events, etc. (They must be initialized later via {@link WolfyUtilsBukkit#initialize()})
     * @return The WolfyUtilities instance for the plugin.
     */
    public WolfyUtilsBukkit getAPI(Plugin plugin, boolean init) {
        return wolfyUtilsInstances.computeIfAbsent(plugin.getName(), s -> new WolfyUtilsBukkit((WolfyCoreBukkit) this, plugin, init));
    }

    /**
     * Gets or create the {@link WolfyUtilsBukkit} instance for the specified plugin.
     * This method also creates the InventoryAPI with the specified custom class of the {@link CustomCache}.<br>
     * <b>You need to run {@link WolfyUtilsBukkit#initialize()} inside your onEnable() </b> to register required events!
     *
     * @param plugin           The plugin to get the instance from.
     * @param customCacheClass The class of the custom cache you created. Must extend {@link CustomCache}
     * @return The WolfyUtilities instance for the plugin.
     */
    public WolfyUtilsBukkit getAPI(Plugin plugin, Class<? extends CustomCache> customCacheClass) {
        return wolfyUtilsInstances.computeIfAbsent(plugin.getName(), s -> new WolfyUtilsBukkit((WolfyCoreBukkit) this, plugin, customCacheClass));
    }

    /**
     * Checks if the specified plugin has an API instance associated with it.
     *
     * @param plugin The plugin to check.
     * @return True in case the API is available; false otherwise.
     */
    public boolean has(Plugin plugin) {
        return wolfyUtilsInstances.containsKey(plugin.getName());
    }

    /**
     * Returns an unmodifiable List of all available {@link WolfyUtilsBukkit} instances.
     *
     * @return A list containing all the created API instances.
     */
    public List<WolfyUtilsBukkit> getAPIList() {
        return List.copyOf(wolfyUtilsInstances.values());
    }

    /**
     * Register a new {@link APIReference.Parser} that can parse ItemStacks and keys from another plugin to a usable {@link APIReference}
     *
     * @param parser an {@link APIReference.Parser} instance.
     * @see CustomItem#registerAPIReferenceParser(APIReference.Parser)
     */
    public abstract void registerAPIReference(APIReference.Parser<?> parser);

}
