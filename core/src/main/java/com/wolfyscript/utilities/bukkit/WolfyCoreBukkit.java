package com.wolfyscript.utilities.bukkit;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.compatibility.CompatibilityManager;
import me.wolfyscript.utilities.compatibility.CompatibilityManagerBukkit;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

/**
 * The core implementation of WolfyUtils.<br>
 * It manages the core plugin of WolfyUtils and there is only one instance of it.<br>
 *
 * If you want to use the plugin specific API, see {@link com.wolfyscript.utilities.common.WolfyUtils} & {@link WolfyUtilsBukkit}
 */
public final class WolfyCoreBukkit extends WolfyUtilCore {

    private BukkitAudiences adventure;

    /**
     * Constructor invoked by Spigot when the plugin is loaded.
     */
    public WolfyCoreBukkit() {
        super();
        api.getChat().setChatPrefix(Component.text("[", NamedTextColor.GRAY).append(Component.text("WU", NamedTextColor.AQUA)).append(Component.text("] ", NamedTextColor.DARK_GRAY)));
    }

    @Override
    protected CompatibilityManager createCompatibilityManager() {
        return new CompatibilityManagerBukkit(this);
    }

    @Override
    @NotNull
    public BukkitAudiences getAdventure() {
        if(this.adventure == null) {
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
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    @Override
    public com.wolfyscript.utilities.common.chat.Chat getChat() {
        return api.getChat();
    }

}
