package com.wolfyscript.utilities.bukkit;

import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManager;
import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManagerBukkit;
import com.wolfyscript.utilities.WolfyCore;

/**
 * The core implementation of WolfyUtils.<br>
 * It manages the core plugin of WolfyUtils and there is only one instance of it.<br>
 *
 * If you want to use the plugin specific API, see {@link com.wolfyscript.utilities.WolfyUtils} & {@link WolfyUtilsBukkit}
 */
public final class WolfyCoreBukkit extends WolfyCoreImpl implements WolfyCore {

    private final CompatibilityManager compatibilityManager;

    /**
     * Constructor invoked by Spigot when the plugin is loaded.
     */
    public WolfyCoreBukkit(WolfyCoreBukkitBootstrap plugin) {
        super(plugin);
        this.platform = new PlatformImpl(this);
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
        return (WolfyCoreBukkit) WolfyCoreBukkitBootstrap.getInstance().getCore();
    }

    @Override
    public CompatibilityManager getCompatibilityManager() {
        return compatibilityManager;
    }

    @Override
    public void load() {
        super.load();
    }

    @Override
    public void enable() {
        super.enable();
        ((PlatformImpl) platform).init();
    }

    @Override
    public void disable() {
        super.disable();
        ((PlatformImpl) platform).unLoad();
    }

}
