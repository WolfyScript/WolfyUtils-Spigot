package com.wolfyscript.utilities.bukkit;

import com.wolfyscript.utilities.bukkit.chat.BukkitChat;
import com.wolfyscript.utilities.bukkit.config.ConfigAPI;
import com.wolfyscript.utilities.bukkit.console.Console;
import com.wolfyscript.utilities.bukkit.gui.InventoryAPI;
import com.wolfyscript.utilities.bukkit.gui.cache.CustomCache;
import com.wolfyscript.utilities.bukkit.language.LangAPISpigot;
import com.wolfyscript.utilities.bukkit.network.messages.MessageAPI;
import com.wolfyscript.utilities.bukkit.nms.api.NMSUtil;
import com.wolfyscript.utilities.bukkit.registry.BukkitRegistries;
import com.wolfyscript.utilities.bukkit.world.items.BookUtil;
import com.wolfyscript.utilities.common.Identifiers;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.language.LanguageAPI;
import com.wolfyscript.utilities.exceptions.InvalidCacheTypeException;
import java.io.File;
import java.util.Locale;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class WolfyUtilsBukkit extends WolfyUtils {

    private final WolfyCoreBukkit core;

    private final Plugin plugin;

    private final BukkitChat chat;
    private final LanguageAPI languageAPI;
    private final Console console;
    private final Permissions permissions;
    private final BookUtil bookUtil;
    private final MessageAPI messageAPI;
    private final NMSUtil nmsUtil;
    private final Identifiers identifiers;

    private String dataBasePrefix;
    private final ConfigAPI configAPI;
    private InventoryAPI<?> inventoryAPI;

    private final boolean initialize;

    WolfyUtilsBukkit(WolfyCoreBukkit core, Plugin plugin, Class<? extends CustomCache> customCacheClass) {
        this(core, plugin, customCacheClass, false);
    }

    WolfyUtilsBukkit(WolfyCoreBukkit core, Plugin plugin, boolean init) {
        this(core, plugin, CustomCache.class, init);
    }

    WolfyUtilsBukkit(WolfyCoreBukkit core, Plugin plugin, Class<? extends CustomCache> cacheType, boolean init) {
        this.core = core;
        this.plugin = plugin;
        this.languageAPI = new LangAPISpigot(this);
        this.chat = new BukkitChat(this);
        this.console = new Console(this);
        this.permissions = new Permissions(this);
        this.bookUtil = new BookUtil(this);
        this.messageAPI = new MessageAPI(this);
        this.nmsUtil = NMSUtil.create(this);
        this.dataBasePrefix = getName().toLowerCase(Locale.ROOT) + "_";
        this.configAPI = new ConfigAPI(this);
        this.inventoryAPI = new InventoryAPI<>(plugin, this, cacheType);
        this.identifiers = new BukkitIdentifiers(this);
        this.initialize = init;
    }

    public final void initialize() {
        Bukkit.getPluginManager().registerEvents(this.inventoryAPI, plugin);
        permissions.initRootPerm(getName().toLowerCase(Locale.ROOT).replace(" ", "_") + ".*");
    }

    @Override
    public WolfyCoreBukkit getCore() {
        return core;
    }

    public BukkitRegistries getRegistries() {
        return core.getRegistries();
    }

    @Override
    public String getName() {
        return plugin.getName();
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }

    /**
     * @return The {@link LanguageAPI} instance.
     * @see LanguageAPI More information about the Language API
     */
    @Override
    public LanguageAPI getLanguageAPI() {
        return languageAPI;
    }

    @Override
    public BukkitChat getChat() {
        return chat;
    }

    @Override
    public Identifiers getIdentifiers() {
        return identifiers;
    }

    /**
     * @return The {@link ConfigAPI} instance.
     * @see ConfigAPI More information about the Config API.
     */
    public ConfigAPI getConfigAPI() {
        return configAPI;
    }

    /**
     * @return The {@link Console} instance.
     * @see Console More information about the Console Util.
     */
    public Console getConsole() {
        return console;
    }

    /**
     * @return The {@link Permissions} instance.
     * @see Permissions More information about Permissions
     */
    public Permissions getPermissions() {
        return permissions;
    }

    /**
     * @return The {@link NMSUtil} instance.
     * @see NMSUtil More information about NMSUtil
     */
    public NMSUtil getNmsUtil() {
        return nmsUtil;
    }

    /**
     * @return The {@link BookUtil} instance.
     * @see BookUtil More information about BookUtil
     */
    public BookUtil getBookUtil() {
        return bookUtil;
    }

    public MessageAPI getMessageAPI() {
        return messageAPI;
    }

    /**
     * This method sets the InventoryAPI.
     * <br>
     * Use this method to set an InventoryAPI instance, that uses a custom cache.
     *
     * @param inventoryAPI The InventoryAPI instance with it's custom cache type.
     * @param <T>          The type of cache that was detected in the instance.
     * @see CustomCache CustomCache which can be extended and used as a custom cache for your GUI.
     * @see InventoryAPI InventoryAPI for more information about it.
     */
    public <T extends CustomCache> void setInventoryAPI(InventoryAPI<T> inventoryAPI) {
        this.inventoryAPI = inventoryAPI;
        if (initialize) {
            initialize();
        }
    }

    /**
     * You can use this method to get the InventoryAPI, if you don't know type of cache it uses.
     *
     * @return The {@link InventoryAPI} with unknown type.
     * @see InventoryAPI
     */
    public InventoryAPI<?> getInventoryAPI() {
        return getInventoryAPI(inventoryAPI.getCacheInstance().getClass());
    }

    /**
     * This method is used to get the InventoryAPI, that uses the type class as the cache.
     * <br>
     * If there is no active {@link InventoryAPI} instance, then this method will create one with the specified type.
     * <br>
     * If there is an active {@link InventoryAPI} instance, then the specified class must be an instance of the cache, else it will throw a {@link InvalidCacheTypeException}.
     *
     * @param type The class of the custom cache.
     * @param <T>  The type of the cache that was detected in the class.
     * @return The {@link InventoryAPI} with the specified type as it's cache.
     * @throws InvalidCacheTypeException If the type class is not an instance of the actual cache specified in the {@link InventoryAPI}.
     */
    public <T extends CustomCache> InventoryAPI<T> getInventoryAPI(Class<T> type) throws InvalidCacheTypeException {
        if (hasInventoryAPI() && type.isInstance(inventoryAPI.getCacheInstance())) {
            return (InventoryAPI<T>) inventoryAPI;
        } else if (!hasInventoryAPI()) {
            InventoryAPI<T> newInventoryAPI = new InventoryAPI<>(plugin, this, type);
            inventoryAPI = newInventoryAPI;
            return newInventoryAPI;
        }
        throw new InvalidCacheTypeException("Cache type " + type.getName() + " expected, got " + inventoryAPI.getCacheInstance().getClass().getName() + "!");
    }

    public boolean hasInventoryAPI() {
        return inventoryAPI != null;
    }

    /**
     * @return The plugin that this WolfyUtilities belongs to.
     */
    public Plugin getPlugin() {
        return plugin;
    }

    public String getDataBasePrefix() {
        return dataBasePrefix;
    }

    public void setDataBasePrefix(String dataBasePrefix) {
        this.dataBasePrefix = dataBasePrefix;
    }
}
