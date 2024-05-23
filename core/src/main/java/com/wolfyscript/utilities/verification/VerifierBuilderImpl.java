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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class VerifierBuilderImpl<T, B extends VerifierBuilder<T, B, R>, R extends Verifier<T>> implements VerifierBuilder<T, B, R> {

    protected final NamespacedKey key;
    protected final VerifierBuilder<?, ?, ?> parentBuilder;
    protected Function<VerifierContainer<T>, VerifierContainer.UpdateStep<T>> validationFunction;
    protected final List<VerifierEntry<T, ?>> childValidators = new ArrayList<>();
    protected boolean required = true;
    protected int requiresOptionals = 0;
    protected Function<VerifierContainer<T>, String> nameConstructorFunction = container -> container.value().map(value -> value.getClass().getSimpleName()).orElse("Unnamed");

    public VerifierBuilderImpl(NamespacedKey key, VerifierBuilder<?, ?, ?> parent) {
        this.key = key;
        this.parentBuilder = parent;
    }

    protected abstract B self();

    @Override
    public B validate(Function<VerifierContainer<T>, VerifierContainer.UpdateStep<T>> validateFunction) {
        this.validationFunction = validateFunction;
        return self();
    }

    @Override
    public B optional() {
        this.required = false;
        return self();
    }

    @Override
    public B name(Function<VerifierContainer<T>, String> nameConstructor) {
        this.nameConstructorFunction = nameConstructor;
        return self();
    }

    @Override
    public B require(int count) {
        this.requiresOptionals = count;
        return self();
    }

    @Override
    public <C> B object(Function<T, C> getter, ObjectVerifier<C> verifier) {
        return object(getter, verifier, cVerifierBuilder -> {});
    }

    @Override
    public <C> B object(Function<T, C> getter, ObjectVerifier<C> verifier, Consumer<ObjectVerifierBuilder<C>> override) {
        var builderComplete = new ObjectVerifierBuilderImpl<>(null, this, verifier);
        override.accept(builderComplete);
        childValidators.add(new VerifierEntry<>(builderComplete.build(), getter));
        return self();
    }

    @Override
    public <C> B object(Function<T, C> getter, Consumer<ObjectVerifierBuilder<C>> childBuilder) {
        var builderComplete = new ObjectVerifierBuilderImpl<C>(null, this);
        childBuilder.accept(builderComplete);
        childValidators.add(new VerifierEntry<>(builderComplete.build(), getter));
        return self();
    }

    @Override
    public <C> B collection(Function<T, Collection<C>> getter, Consumer<CollectionVerifierBuilder<C>> childBuilder) {
        var builderComplete = new CollectionVerifierBuilderImpl<C>(null, this);
        childBuilder.accept(builderComplete);
        childValidators.add(new VerifierEntry<>(builderComplete.build(), getter));
        return self();
    }

}
