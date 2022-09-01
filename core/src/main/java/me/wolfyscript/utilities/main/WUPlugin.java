package me.wolfyscript.utilities.main;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.messages.MessageFactory;
import me.wolfyscript.utilities.messages.MessageHandler;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

/**
 * Exists only for backwards compatibility.
 */
@Deprecated
public abstract class WUPlugin extends WolfyUtilCore {

    @Deprecated
    private static WUPlugin instance;

    public WUPlugin() {
        super();
        instance = this;
    }

    public WUPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        instance = this;
    }

    @Deprecated
    public abstract WolfyUtilities getWolfyUtilities();

    @Deprecated
    public static WUPlugin getInstance() {
        return instance;
    }

    @Deprecated
    public void loadParticleEffects(){ }

    public abstract MessageHandler getMessageHandler();

    public abstract MessageFactory getMessageFactory();
}
