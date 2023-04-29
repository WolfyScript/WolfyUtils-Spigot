package com.wolfyscript.utilities.paper;

import com.wolfyscript.utilities.bukkit.WolfyCoreImpl;
import com.wolfyscript.utilities.bukkit.WolfyUtilBootstrap;
import com.wolfyscript.utilities.bukkit.chat.BukkitChat;
import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManager;
import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManagerBukkit;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.jetbrains.annotations.NotNull;

public final class WolfyCorePaper extends WolfyCoreImpl {

    private BukkitAudiences adventure;
    private final CompatibilityManager compatibilityManager;

    /**
     * Constructor invoked by Spigot when the plugin is loaded.
     */
    public WolfyCorePaper(WolfyUtilBootstrap bootstrap) {
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

    @NotNull
    public BukkitAudiences getAdventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        this.adventure = BukkitAudiences.create(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    @Override
    public BukkitChat getChat() {
        return getWolfyUtils().getChat();
    }
}
