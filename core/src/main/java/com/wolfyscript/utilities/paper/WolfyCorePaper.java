package com.wolfyscript.utilities.paper;

import com.wolfyscript.utilities.bukkit.WolfyCoreImpl;
import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManager;
import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManagerBukkit;
import net.kyori.adventure.Adventure;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.Audiences;
import net.kyori.adventure.internal.properties.AdventureProperties;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public final class WolfyCorePaper extends WolfyCoreImpl {

    private final CompatibilityManager compatibilityManager;

    /**
     * Constructor invoked by Spigot when the plugin is loaded.
     */
    public WolfyCorePaper(WolfyCorePaperBootstrap bootstrap) {
        super(bootstrap);
        this.compatibilityManager = new CompatibilityManagerBukkit(this);
    }

    /**
     * Gets an instance of the core plugin.
     * <strong>Only use this if necessary! First try to get the instance via your {@link com.wolfyscript.utilities.common.WolfyUtils} instance!</strong>
     *
     * @return The instance of the core.
     */
    @Deprecated
    public static WolfyCorePaper getInstance() {
        return (WolfyCorePaper) WolfyCoreImpl.getInstance();
    }

    @Override
    public CompatibilityManager getCompatibilityManager() {
        return compatibilityManager;
    }

}
