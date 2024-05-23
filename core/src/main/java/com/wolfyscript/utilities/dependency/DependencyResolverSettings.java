package com.wolfyscript.utilities.dependency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify the {@link DependencyResolver} for the annotated type.
 * <p>
 * This is used for classes & values that may depend on something on their own.
 * Dependencies are resolved by crawling over the tree of classes & values, that are annotated with {@link DependencyResolverSettings} and {@link DependencySource}.
 * </p>
 * <p>
 * This annotation is only necessary when the annotated type (or its instances) provide a list of their own dependencies.
 * If you are looking for propagating dependencies from the fields use {@link DependencySource} to annotate the fields instead!
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DependencyResolverSettings {

    /**
     * Specifies the type of the {@link DependencyResolver}
     *
     * @return The type of the {@link DependencyResolver} to use
     */
    Class<? extends DependencyResolver> value();

}
