package com.wolfyscript.utilities.paper;

import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class WolfyUtilsBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull PluginProviderContext context) {
        // Do nothing yet
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return PluginBootstrap.super.createPlugin(context);
    }
}
