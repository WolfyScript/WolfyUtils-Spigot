/*
 *       ____ _  _ ____ ___ ____ _  _ ____ ____ ____ ____ ___ _ _  _ ____
 *       |    |  | [__   |  |  | |\/| |    |__/ |__| |___  |  | |\ | | __
 *       |___ |__| ___]  |  |__| |  | |___ |  \ |  | |     |  | | \| |__]
 *
 *       CustomCrafting Recipe creation and management tool for Minecraft
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wolfyscript.utilities.verification;

import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public interface VerifierBuilder<T> {

    /**
     * Initiates the builder for object validation
     *
     * @param <T> The type of the object
     * @return The init step for the validator builder
     */
    static <T> ObjectVerifierBuilder<T> object(NamespacedKey key) {
        return new ObjectVerifierBuilderImpl<>(key, null);
    }


    static <T> ObjectVerifierBuilder<T> object(NamespacedKey key, Verifier<T> extend) {
        if (!(extend instanceof ObjectVerifierImpl<T> objectVerifier)) throw new IllegalArgumentException("Validator must be an object validator!");
        return new ObjectVerifierBuilderImpl<>(key, null, objectVerifier);
    }

    /**
     * Initiates the builder for collection validation
     *
     * @param <T> The type of the object contained in the collection
     * @return This init step for the validator builder
     */
    static <T> CollectionVerifierBuilder<T> collection(NamespacedKey key) {
        return new CollectionVerifierBuilderImpl<>(key, null);
    }

    static <T> CollectionVerifierBuilder<T> collection(NamespacedKey key, Verifier<T> extend) {

        return new CollectionVerifierBuilderImpl<>(key, null);
    }

    /**
     * Specifies the validation function that validates the value.
     *
     * @param validateFunction The validation function
     * @return This builder instance for chaining
     */
    VerifierBuilder<T> validate(Function<VerifierContainer<T>, VerifierContainer.UpdateStep<T>> validateFunction);

    VerifierBuilder<T> name(Function<VerifierContainer<T>, String> nameConstructor);

    default VerifierBuilder<T> name(String name) {
        return name(tVerifierContainer -> name);
    }

    VerifierBuilder<T> optional();

    VerifierBuilder<T> require(int count);

    /**
     * Adds a nested child object validation to this validator.
     * The getter provides a way to compute the child value from the current value.
     *
     * @param getter       The getter to get the child value
     * @param childBuilder The builder for the child validator
     * @param <C>          The child value type
     * @return This builder instance for chaining
     */
    <C> VerifierBuilder<T> object(Function<T, C> getter, Consumer<VerifierBuilder<C>> childBuilder);

    default <C> VerifierBuilder<T> object(Function<T, C> getter, Verifier<C> verifier) {
        return object(getter, verifier, cVerifierBuilder -> {});
    }

    <C> VerifierBuilder<T> object(Function<T, C> getter, Verifier<C> verifier, Consumer<VerifierBuilder<C>> override);

    /**
     * Adds a nested child collection validator. The getter provides a way to compute the collection from the current value.
     *
     * @param getter       The getter to get the collection
     * @param childBuilder The builder for the child validator
     * @param <C>          The type of the collection elements
     * @return This builder for chaining
     */
    <C> VerifierBuilder<T> collection(Function<T, Collection<C>> getter, Consumer<CollectionVerifierBuilder<C>> childBuilder);

    Verifier<T> build();

    /**
     * Initiates the builder to use with default settings, or use existing validators.
     *
     * @param <T> The type of the value
     * @param <B> The type of the builder
     */
    interface InitStep<T, B extends VerifierBuilder<T>> {

        /**
         * Uses the default builder as is without any extensions or manipulations.
         *
         * @return The default builder as is
         */
        B def();

        /**
         * Uses an existing validator together with the validator build using this builder.
         * Basically allowing to extend the existing validator with more child validators, etc.<br>
         * Extending it is not required, it can simply be used to reuse validators for child objects, etc.
         *
         * @param verifier The existing validator that handles the super type of the type handled by this builder
         * @return A builder based on the specified validator
         */
        B use(Verifier<T> verifier);

    }

}
