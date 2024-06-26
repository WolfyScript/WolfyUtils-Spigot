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

import java.util.EnumMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

class ObjectVerifierImpl<T_VALUE> implements ObjectVerifier<T_VALUE> {

    private final NamespacedKey key;
    final boolean required;
    final int requiredOptional;
    protected final Consumer<VerificationResult.Builder<T_VALUE>> resultFunction;
    protected final List<VerifierEntry<T_VALUE, ?>> childValidators;
    protected Function<VerificationResult<T_VALUE>, String> nameConstructorFunction;

    public ObjectVerifierImpl(NamespacedKey key, boolean required, int requiredOptional, Function<VerificationResult<T_VALUE>, String> nameConstructorFunction, Consumer<VerificationResult.Builder<T_VALUE>> resultFunction, List<VerifierEntry<T_VALUE, ?>> childValidators) {
        this.key = key;
        this.required = required;
        this.requiredOptional = requiredOptional;
        this.resultFunction = resultFunction;
        this.childValidators = childValidators;
        this.nameConstructorFunction = nameConstructorFunction;
    }

    @Override
    public boolean optional() {
        return !required;
    }

    @Override
    public String getNameFor(VerificationResult<T_VALUE> container) {
        return nameConstructorFunction.apply(container);
    }

    @Override
    public VerificationResult<T_VALUE> validate(T_VALUE value) {
        var container = new VerificationResultImpl.BuilderImpl<>(this, value);

        VerificationResult.ResultType requiredType = VerificationResult.ResultType.UNKNOWN;
        EnumMap<VerificationResult.ResultType, Integer> optionalCounts = new EnumMap<>(VerificationResult.ResultType.class);

        for (VerifierEntry<T_VALUE, ?> entry : childValidators) {
            VerificationResult<?> result = entry.applyNestedValidator(value);
            container.children(List.of(result));
            if (entry.verifier().optional()) {
                optionalCounts.merge(result.type(), 1, Integer::sum);
                continue;
            }
            requiredType = requiredType.and(result.type());
        }
        if (optionalCounts.getOrDefault(VerificationResult.ResultType.VALID, 0) >= requiredOptional) {
            requiredType = requiredType.and(VerificationResult.ResultType.VALID);
        } else {
            requiredType = requiredType.and(VerificationResult.ResultType.INVALID);
        }

        container.type(requiredType);

        if (resultFunction != null) {
            resultFunction.accept(container);
        }

        return container.complete();
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }

    @Override
    public String toString() {
        return "ValidatorImpl{" +
                "key=" + key +
                '}';
    }

}
