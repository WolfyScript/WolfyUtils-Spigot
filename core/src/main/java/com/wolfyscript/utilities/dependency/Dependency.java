package com.wolfyscript.utilities.dependency;

/**
 * Represents a Dependency of a type/instance.
 * This interface may be implemented and then provided via a custom {@link DependencyResolver} implementation.
 */
public interface Dependency {

    /**
     * Checks if the dependency is loaded and available.
     *
     * @return true when available; false otherwise
     */
    boolean isAvailable();

}
