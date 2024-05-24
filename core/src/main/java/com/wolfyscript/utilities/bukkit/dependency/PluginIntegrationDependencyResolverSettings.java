package com.wolfyscript.utilities.bukkit.dependency;

import com.wolfyscript.utilities.dependency.DependencyResolverSettings;
import me.wolfyscript.utilities.compatibility.PluginIntegration;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PluginIntegrationDependencyResolverSettings {

    String pluginName();

    Class<? extends PluginIntegration> integration();

}
