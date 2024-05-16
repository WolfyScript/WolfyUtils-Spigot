package com.wolfyscript.utilities.bukkit.dependency;

import com.wolfyscript.utilities.dependency.DependencyResolverSettings;
import me.wolfyscript.utilities.compatibility.PluginIntegration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@DependencyResolverSettings(PluginIntegrationDependencyResolver.class)
public @interface PluginIntegrationDependencyResolverSettings {

    String pluginName();

    Class<? extends PluginIntegration> integration();

}
