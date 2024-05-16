package com.wolfyscript.utilities.bukkit.dependency;

import com.wolfyscript.utilities.dependency.Dependency;
import me.wolfyscript.utilities.compatibility.PluginIntegration;

import java.util.Objects;

public class PluginIntegrationDependency implements Dependency {

    private final PluginIntegration pluginIntegration;

    public PluginIntegrationDependency(PluginIntegration pluginIntegration) {
        this.pluginIntegration = pluginIntegration;
    }

    @Override
    public boolean isAvailable() {
        return pluginIntegration.isDoneLoading();
    }

    public PluginIntegration getPluginIntegration() {
        return pluginIntegration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PluginIntegrationDependency that = (PluginIntegrationDependency) o;
        return Objects.equals(pluginIntegration, that.pluginIntegration);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pluginIntegration);
    }
}
