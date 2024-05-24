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

import java.util.List;

class ObjectVerifierBuilderImpl<T> extends VerifierBuilderImpl<T, ObjectVerifierBuilder<T>, ObjectVerifier<T>> implements ObjectVerifierBuilder<T> {

    public ObjectVerifierBuilderImpl(NamespacedKey key, VerifierBuilder<?, ?, ?> parent) {
        super(key, parent);
    }

    @Override
    protected ObjectVerifierBuilder<T> self() {
        return this;
    }

    public ObjectVerifierBuilderImpl(NamespacedKey key, VerifierBuilder<?, ?, ?> parent, ObjectVerifier<T> other) {
        this(key, parent);
        if (!(other instanceof ObjectVerifierImpl<T> otherImpl)) { return; }
        this.validationFunction = otherImpl.resultFunction;
        this.childValidators.addAll(otherImpl.childValidators);
        this.required = otherImpl.required;
        this.requiresOptionals = otherImpl.requiredOptional;
        this.nameConstructorFunction = otherImpl.nameConstructorFunction;
    }

    @Override
    public ObjectVerifier<T> build() {
        return new ObjectVerifierImpl<>(key, required, requiresOptionals, nameConstructorFunction, validationFunction, List.copyOf(childValidators));
    }
}
