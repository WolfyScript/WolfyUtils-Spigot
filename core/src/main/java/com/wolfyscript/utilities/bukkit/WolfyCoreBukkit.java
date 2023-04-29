package com.wolfyscript.utilities.bukkit;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wolfyscript.jackson.dataformat.hocon.HoconMapper;
import com.wolfyscript.utilities.Platform;
import com.wolfyscript.utilities.bukkit.chat.BukkitChat;
import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManager;
import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManagerBukkit;
import com.wolfyscript.utilities.bukkit.gui.TestGUI;
import com.wolfyscript.utilities.common.WolfyCore;
import org.reflections.Reflections;

/**
 * The core implementation of WolfyUtils.<br>
 * It manages the core plugin of WolfyUtils and there is only one instance of it.<br>
 *
 * If you want to use the plugin specific API, see {@link com.wolfyscript.utilities.common.WolfyUtils} & {@link WolfyUtilsBukkit}
 */
public final class WolfyCoreBukkit extends WolfyCoreImpl implements WolfyCore {

    private final WolfyUtilsBukkit api;
    private final CompatibilityManager compatibilityManager;

    /**
     * Constructor invoked by Spigot when the plugin is loaded.
     */
    public WolfyCoreBukkit(WolfyUtilBootstrap plugin) {
        super(plugin);
        this.api = getOrCreate(plugin);
        this.compatibilityManager = new CompatibilityManagerBukkit(this);
    }

    /**
     * Gets an instance of the core plugin.
     * <strong>Only use this if necessary! First try to get the instance via your {@link WolfyUtilsBukkit} instance!</strong>
     *
     * @return The instance of the core.
     */
    @Deprecated
    public static WolfyCoreBukkit getInstance() {
        return WolfyUtilBootstrap.getInstance().getCore();
    }

    @Override
    public CompatibilityManager getCompatibilityManager() {
        return compatibilityManager;
    }

    /**
     * Gets the {@link Reflections} instance of the plugins' package.
     *
     * @return The Reflection of the plugins' package.
     */
    @Override
    public WolfyUtilsBukkit getWolfyUtils() {
        return api;
    }

    @Override
    public void load() {
        super.load();
    }

    @Override
    public void enable() {
        super.enable();
        TestGUI testGUI = new TestGUI(this);

        plugin.saveResource("com/wolfyscript/utilities/common/gui/counter/counter_router.conf", true);
        plugin.saveResource("com/wolfyscript/utilities/common/gui/counter/main_menu.conf", true);
        testGUI.initWithConfig();
    }

    @Override
    public void disable() {
        super.disable();
    }

    @Override
    public BukkitChat getChat() {
        return api.getChat();
    }

}
