package com.wolfyscript.utilities.spigot;

import com.wolfyscript.utilities.common.WolfyCore;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.chat.Chat;
import com.wolfyscript.utilities.spigot.language.LangAPISpigot;
import me.wolfyscript.utilities.api.Permissions;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.ChatImpl;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.console.Console;
import me.wolfyscript.utilities.api.inventory.BookUtil;
import me.wolfyscript.utilities.api.inventory.gui.InventoryAPI;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.network.messages.MessageAPI;
import me.wolfyscript.utilities.api.nms.NMSUtil;
import me.wolfyscript.utilities.registry.Registries;
import me.wolfyscript.utilities.util.exceptions.InvalidCacheTypeException;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Locale;

public class WolfyUtilsSpigot extends WolfyUtils {

    private final WolfyCoreSpigot core;

    private final Plugin plugin;

    private final Chat chat;
    private final LanguageAPI languageAPI;
    private final Console console;
    private final ItemUtils itemUtils;
    private final Permissions permissions;
    private final BookUtil bookUtil;
    private final MessageAPI messageAPI;
    private final NMSUtil nmsUtil;

    private String dataBasePrefix;
    private final ConfigAPI configAPI;
    private InventoryAPI<?> inventoryAPI;

    private final boolean initialize;

    protected WolfyUtilsSpigot(WolfyCoreSpigot core, Plugin plugin, Class<? extends CustomCache> cacheType, boolean init) {
        this.core = core;
        this.plugin = plugin;
        this.languageAPI = new LangAPISpigot(this);
        this.chat = new ChatImpl(this);
        this.console = new Console((WolfyUtilities) this);
        this.itemUtils = new ItemUtils((WolfyUtilities) this);
        this.permissions = new Permissions(this);
        this.bookUtil = new BookUtil((WolfyUtilities) this);
        this.messageAPI = new MessageAPI((WolfyUtilities) this);
        this.nmsUtil = NMSUtil.create((WolfyUtilities) this);
        this.dataBasePrefix = getName().toLowerCase(Locale.ROOT) + "_";
        this.configAPI = new ConfigAPI((WolfyUtilities) this);
        this.inventoryAPI = new InventoryAPI<>(plugin, (WolfyUtilities) this, cacheType);
        this.initialize = init;
    }

    public final void initialize() {
        Bukkit.getPluginManager().registerEvents(this.inventoryAPI, plugin);
    }

    @Override
    public WolfyCore getCore() {
        return core;
    }

    public Registries getRegistries() {
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

    /**
     * @return The {@link LanguageAPI} instance.
     * @see LanguageAPI More information about the Language API
     */
    @Override
    public LanguageAPI getLanguageAPI() {
        return languageAPI;
    }

    @Override
    public Chat getChat() {
        return chat;
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
     * @return The {@link ItemUtils} instance.
     * @see ItemUtils More information about ItemUtils.
     */
    public ItemUtils getItemUtils() {
        return itemUtils;
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
            InventoryAPI<T> newInventoryAPI = new InventoryAPI<>(plugin, (WolfyUtilities) this, type);
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
