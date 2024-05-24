package com.wolfyscript.utilities.dependency;

import com.wolfyscript.utilities.json.jackson.MissingDependencyException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Resolves the dependencies of a given type-value pair.
 * Usually used in conjunction with {@link DependencyResolverSettings}
 * @see DependencyResolverSettings
 */
public interface DependencyResolver {

    /**
     * Resolves the dependencies for the specified type and value.
     * This creates a collection of all the dependencies this type and value depend on.
     *
     * @param value The value for which to get the dependencies
     * @param type  The type for which to get the dependencies
     * @return A collection of all dependencies of the given type & value
     */
    Collection<Dependency> resolve(Object value, Class<?> type);

    /**
     * Resolves the dependencies for the given type and value, plus all fields that may propagate their dependencies.
     * <p>
     *     When the class of a field is annotated with {@link DependencyResolverSettings} then its dependencies are always included, otherwise they are ignored.<br>
     *     To include the dependencies even when not directly available, annotate the field with {@link DependencySource},
     *     this way this method will crawl the class & value of that field and look for further fields (recursive).
     * </p>
     *
     * @param value The value to resolve
     * @param type  The type to resolve
     * @return A set of dependencies that this type and all included children depend on
     * @param <T> The type of the value
     */
    static <T> Set<Dependency> resolveDependenciesFor(T value, Class<? extends T> type) {
        final Set<Dependency> dependencies = new HashSet<>();

        if (type.isAnnotationPresent(DependencyResolverSettings.class)) {
            var annotation = type.getAnnotation(DependencyResolverSettings.class);
            try {
                var constructor = annotation.value().getConstructor();

                constructor.setAccessible(true);
                DependencyResolver resolver = constructor.newInstance();
                dependencies.addAll(resolver.resolve(value, type));

            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new MissingDependencyException("Could not resolve dependency resolver settings", e);
            }

        }

        for (Field declaredField : type.getDeclaredFields()) {
            if (declaredField.trySetAccessible()) {
                // Field type is directly annotated with resolver settings
                if (declaredField.getType().isAnnotationPresent(DependencyResolverSettings.class)) {
                    try {
                        var object = declaredField.get(value);
                        dependencies.addAll(resolveDependenciesFor(object, object.getClass()));
                    } catch (IllegalAccessException e) {
                        throw new MissingDependencyException("Failed to fetch dependencies of type '" + declaredField.getType().getName() + "'!", e);
                    }
                }

                // Field type isn't providing dependencies directly, but may propagate dependencies
                DependencySource dependencySource = declaredField.getAnnotation(DependencySource.class);
                if (dependencySource != null) {
                    try {
                        if (dependencySource.flattenIterable() && Iterable.class.isAssignableFrom(declaredField.getType())) {
                            Iterable<?> iterable = (Iterable<?>) declaredField.get(value);
                            for (Object object : iterable) {
                                dependencies.addAll(resolveDependenciesFor(object, object.getClass()));
                            }
                        } else {
                            var object = declaredField.get(value);
                            dependencies.addAll(resolveDependenciesFor(object, object.getClass()));
                        }
                    } catch (IllegalAccessException e) {
                        throw new MissingDependencyException("Failed to fetch dependencies of type '" + declaredField.getType().getName() + "'!", e);
                    }
                }
            }
        }

        Class<?> superType = type.getSuperclass();
        if (superType != null) {
            dependencies.addAll(resolveDependenciesFor(value, superType));
        }

        return dependencies;
    }

}
