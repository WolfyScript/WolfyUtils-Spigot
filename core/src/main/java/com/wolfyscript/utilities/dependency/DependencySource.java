package com.wolfyscript.utilities.dependency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as a source of possible dependencies, that should be crawled when resolving dependencies.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DependencySource {

    /**
     * Specifies if this field should be iterated when it is {@link Iterable}.
     * <p>
     *     When true it iterates over the values and crawls each value (& its type).<br>
     *     When false it treats the field and crawls the types fields instead.
     * </p>
     *
     * @return true if the resolver should iterate the values of this field; false when to treat this as a normal type
     */
    boolean flattenIterable() default true;

}
