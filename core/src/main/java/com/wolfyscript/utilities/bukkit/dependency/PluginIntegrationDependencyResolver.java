package com.wolfyscript.utilities.bukkit.dependency;

import com.wolfyscript.utilities.dependency.Dependency;
import com.wolfyscript.utilities.dependency.DependencyResolver;
import com.wolfyscript.utilities.json.jackson.MissingDependencyException;
import me.wolfyscript.utilities.api.WolfyUtilCore;

import java.util.Optional;

public class PluginIntegrationDependencyResolver implements DependencyResolver {

    @Override
    public Optional<Dependency> resolve(Object value, Class<?> type) {
        var pluginsCompat = WolfyUtilCore.getInstance().getCompatibilityManager().getPlugins();

        if (type.isAnnotationPresent(PluginIntegrationDependencyResolverSettings.class)) {
            var annotation = type.getAnnotation(PluginIntegrationDependencyResolverSettings.class);
            var integration = pluginsCompat.getIntegration(annotation.pluginName(), annotation.integration());
            if (integration == null) {
                throw new MissingDependencyException("Declared integration dependency '" + annotation.pluginName() + "' not found for type '" + type.getName() + "'!");
            }
            return Optional.of(new PluginIntegrationDependency(integration));
        }

        return Optional.empty();
    }
}
