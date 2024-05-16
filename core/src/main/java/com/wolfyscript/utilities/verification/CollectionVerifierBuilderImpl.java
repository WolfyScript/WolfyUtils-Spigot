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

class CollectionVerifierBuilderImpl<T> extends VerifierBuilderImpl<Collection<T>> implements CollectionVerifierBuilder<T> {

    private Verifier<T> elementVerifier;

    CollectionVerifierBuilderImpl(NamespacedKey key, VerifierBuilder<?> parent) {
        super(key, parent);
    }

    CollectionVerifierBuilderImpl(NamespacedKey key, VerifierBuilder<?> parent, CollectionVerifierImpl<T> other) {
        super(key, parent);
        this.elementVerifier = other.elementVerifier;
        this.nameConstructorFunction = other.nameConstructorFunction;
        this.validationFunction = other.resultFunction;
        this.required = other.required;
        this.requiresOptionals = other.requiredOptional;
    }

    @Override
    public CollectionVerifierBuilder<T> forEach(Consumer<VerifierBuilder<T>> childBuilderFunction) {
        var builder = VerifierBuilder.<T>object(key);
        childBuilderFunction.accept(builder);
        elementVerifier = builder.build();
        return this;
    }

    @Override
    public CollectionVerifierBuilder<T> forEach(Verifier<T> existing) {
        elementVerifier = existing;
        return this;
    }

    @Override
    public CollectionVerifierBuilder<T> validate(Function<VerifierContainer<Collection<T>>, VerifierContainer.UpdateStep<Collection<T>>> validateFunction) {
        return (CollectionVerifierBuilder<T>) super.validate(validateFunction);
    }

    @Override
    public CollectionVerifierBuilder<T> name(Function<VerifierContainer<Collection<T>>, String> nameConstructor) {
        return (CollectionVerifierBuilder<T>) super.name(nameConstructor);
    }

    @Override
    public Verifier<Collection<T>> build() {
        return new CollectionVerifierImpl<>(key, required, requiresOptionals, nameConstructorFunction, validationFunction, elementVerifier);
    }
}
