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

public interface VerifierBuilder<T, B extends VerifierBuilder<T, B, R>, R extends Verifier<T>> {

    /**
     * Initiates the builder for object validation
     *
     * @param <T> The type of the object
     * @return The init step for the validator builder
     */
    static <T> ObjectVerifierBuilder<T> object(NamespacedKey key) {
        return new ObjectVerifierBuilderImpl<>(key, null);
    }

    static <T> ObjectVerifierBuilder<T> object(NamespacedKey key, ObjectVerifier<T> extend) {
        return new ObjectVerifierBuilderImpl<>(key, null, extend);
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

    static <T> CollectionVerifierBuilder<T> collection(NamespacedKey key, CollectionVerifier<T> extend) {
        return new CollectionVerifierBuilderImpl<>(key, null, extend);
    }

    /**
     * Specifies the validation function that validates the value.
     *
     * @param validateFunction The validation function
     * @return This builder instance for chaining
     */
    B validate(Consumer<VerificationResult.Builder<T>> validateFunction);

    B name(Function<VerificationResult<T>, String> nameConstructor);

    default B name(String name) {
        return name(tVerifierContainer -> name);
    }

    B optional();

    B require(int count);

    /**
     * Adds a nested child object validation to this validator.
     * The getter provides a way to compute the child value from the current value.
     *
     * @param getter       The getter to get the child value
     * @param childBuilder The builder for the child validator
     * @param <C>          The child value type
     * @return This builder instance for chaining
     */
    <C> B object(Function<T, C> getter, Consumer<ObjectVerifierBuilder<C>> childBuilder);

    default <C> B object(Function<T, C> getter, ObjectVerifier<C> verifier) {
        return object(getter, verifier, cVerifierBuilder -> {});
    }

    <C> B object(Function<T, C> getter, ObjectVerifier<C> verifier, Consumer<ObjectVerifierBuilder<C>> override);

    /**
     * Adds a nested child collection validator. The getter provides a way to compute the collection from the current value.
     *
     * @param getter       The getter to get the collection
     * @param childBuilder The builder for the child validator
     * @param <C>          The type of the collection elements
     * @return This builder for chaining
     */
    <C> B collection(Function<T, Collection<C>> getter, Consumer<CollectionVerifierBuilder<C>> childBuilder);

    R build();

}
