package com.wolfyscript.utilities.bukkit;

import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManager;
import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManagerBukkit;
import com.wolfyscript.utilities.common.WolfyCore;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.util.Services;
import org.jetbrains.annotations.NotNull;

/**
 * The core implementation of WolfyUtils.<br>
 * It manages the core plugin of WolfyUtils and there is only one instance of it.<br>
 *
 * If you want to use the plugin specific API, see {@link com.wolfyscript.utilities.common.WolfyUtils} & {@link WolfyUtilsBukkit}
 */
public final class WolfyCoreBukkit extends WolfyCoreImpl implements WolfyCore {

    private BukkitAudiences adventure;
    private final CompatibilityManager compatibilityManager;

    /**
     * Constructor invoked by Spigot when the plugin is loaded.
     */
    public WolfyCoreBukkit(WolfyCoreBukkitBootstrap plugin) {
        super(plugin);
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

    @NotNull
    public BukkitAudiences getAdventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    @Override
    public void load() {
        super.load();
    }

    @Override
    public void enable() {
        this.adventure = BukkitAudiences.create(getPlugin());
        super.enable();
    }

    @Override
    public void disable() {
        super.disable();
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

}
