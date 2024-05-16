package com.wolfyscript.utilities.dependency;

import com.wolfyscript.utilities.json.jackson.MissingDependencyException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public interface DependencyResolver {

    Optional<Dependency> resolve(Object value, Class<?> type);

    static <T> Set<Dependency> resolveDependenciesFor(T value, Class<? extends T> type) {
        final Set<Dependency> dependencies = new HashSet<>();

        if (type.isAnnotationPresent(DependencyResolverSettings.class)) {
            var annotation = type.getAnnotation(DependencyResolverSettings.class);
            try {
                var constructor = annotation.value().getConstructor();

                constructor.setAccessible(true);
                DependencyResolver resolver = constructor.newInstance();
                resolver.resolve(value, type).ifPresent(dependencies::add);

            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new MissingDependencyException("Could not resolve dependency resolver settings", e);
            }

        }

        for (Field declaredField : type.getDeclaredFields()) {
            if (declaredField.trySetAccessible()) {
                DependencySource dependencySource = declaredField.getAnnotation(DependencySource.class);
                if (dependencySource != null) {
                    try {
                        if (dependencySource.flattenIterable() && Iterable.class.isAssignableFrom(declaredField.getType())) {
                            Iterable<?> iterable = (Iterable<?>) declaredField.get(value);
                            for (Object object : iterable) {
                                dependencies.addAll(resolveDependenciesFor(object, object.getClass()));
                            }
                        } else {
                            Object object = declaredField.get(value);
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
