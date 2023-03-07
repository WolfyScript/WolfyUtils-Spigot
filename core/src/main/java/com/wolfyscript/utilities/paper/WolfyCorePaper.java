package com.wolfyscript.utilities.paper;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.compatibility.CompatibilityManager;
import me.wolfyscript.utilities.compatibility.CompatibilityManagerBukkit;
import net.kyori.adventure.Adventure;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public final class WolfyCorePaper extends WolfyUtilCore {

    private BukkitAudiences adventure;

    /**
     * Constructor invoked by Spigot when the plugin is loaded.
     */
    public WolfyCorePaper() {
        super();
        api.getChat().setChatPrefix(Component.text("[", NamedTextColor.GRAY).append(Component.text("WU", NamedTextColor.AQUA)).append(Component.text("] ", NamedTextColor.DARK_GRAY)));
    }

    @Override
    protected CompatibilityManager createCompatibilityManager() {
        return new CompatibilityManagerBukkit(this);
    }

    /**
     * Gets an instance of the core plugin.
     * <strong>Only use this if necessary! First try to get the instance via your {@link com.wolfyscript.utilities.common.WolfyUtils} instance!</strong>
     *
     * @return The instance of the core.
     */
    @Deprecated
    public static WolfyCorePaper getInstance() {
        return (WolfyCorePaper) WolfyUtilCore.getInstance();
    }

    @Override
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
    public com.wolfyscript.utilities.common.chat.Chat getChat() {
        return api.getChat();
    }

}
