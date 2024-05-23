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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class CollectionVerifierImpl<T_VALUE> implements CollectionVerifier<T_VALUE> {

    private final NamespacedKey key;
    final boolean required;
    final int requiredOptional;
    protected final Function<VerifierContainer<Collection<T_VALUE>>, VerifierContainer.UpdateStep<Collection<T_VALUE>>> resultFunction;
    protected final Verifier<T_VALUE> elementVerifier;
    protected Function<VerifierContainer<Collection<T_VALUE>>, String> nameConstructorFunction;

    public CollectionVerifierImpl(NamespacedKey key, boolean required, int requiredOptional, Function<VerifierContainer<Collection<T_VALUE>>, String> nameConstructorFunction, Function<VerifierContainer<Collection<T_VALUE>>, VerifierContainer.UpdateStep<Collection<T_VALUE>>> resultFunction, Verifier<T_VALUE> elementVerifier) {
        this.key = key;
        this.required = required;
        this.requiredOptional = requiredOptional;
        this.resultFunction = resultFunction;
        this.elementVerifier = elementVerifier;
        this.nameConstructorFunction = nameConstructorFunction;
    }

    @Override
    public Verifier<T_VALUE> getElementVerifier() {
        return elementVerifier;
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }

    @Override
    public String getNameFor(VerifierContainer<Collection<T_VALUE>> container) {
        return nameConstructorFunction.apply(container);
    }

    @Override
    public VerifierContainerImpl<Collection<T_VALUE>> validate(Collection<T_VALUE> values) {
        VerifierContainerImpl<Collection<T_VALUE>> container = new VerifierContainerImpl<>(values, this);

        VerifierContainer.ResultType resultType;
        if (elementVerifier.optional()) {
            Map<VerifierContainer.ResultType, Integer> counts = new EnumMap<>(VerifierContainer.ResultType.class);
            for (T_VALUE value : values) {
                VerifierContainer<T_VALUE> result = elementVerifier.validate(value);
                container.update().children(List.of(result));
                counts.merge(result.type(), 1, Integer::sum);
            }

            if (counts.getOrDefault(VerifierContainer.ResultType.VALID, 0) >= requiredOptional) {
                resultType = VerifierContainer.ResultType.VALID;
            } else {
                resultType = VerifierContainer.ResultType.INVALID;
            }
        } else {
            resultType = values.stream()
                    .map(value -> {
                        VerifierContainer<T_VALUE> result = elementVerifier.validate(value);
                        container.update().children(List.of(result));
                        return result.type();
                    })
                    .distinct()
                    .reduce(VerifierContainer.ResultType::and)
                    .orElse(VerifierContainer.ResultType.INVALID);
        }

        container.update().type(resultType);

        if (resultFunction != null) {
            resultFunction.apply(container);
        }

        return container;
    }

    @Override
    public boolean optional() {
        return !required;
    }
}
