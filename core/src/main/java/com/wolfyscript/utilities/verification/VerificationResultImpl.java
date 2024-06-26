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

import java.util.*;
import java.util.function.Consumer;

class VerificationResultImpl<T> implements VerificationResult<T> {

    private final ResultType type;
    private final Set<String> faults;
    private final T value;
    private final Verifier<T> verifier;
    private final List<VerificationResult<?>> children;

    private VerificationResultImpl(T value, Verifier<T> verifier, ResultType type, Set<String> faults, List<VerificationResult<?>> children) {
        this.type = type;
        this.value = value;
        this.faults = Collections.unmodifiableSet(faults);
        this.verifier = verifier;
        this.children = Collections.unmodifiableList(children);
    }

    @Override
    public String getName() {
        return verifier.getNameFor(this);
    }

    @Override
    public boolean optional() {
        return verifier.optional();
    }

    @Override
    public List<VerificationResult<?>> children() {
        return children;
    }

    @Override
    public Optional<T> value() {
        return Optional.ofNullable(value);
    }

    @Override
    public ResultType type() {
        return type;
    }

    @Override
    public Collection<String> faults() {
        return faults;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (VerificationResultImpl<?>) obj;
        return Objects.equals(this.type, that.type) &&
                Objects.equals(this.faults, that.faults);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, faults);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        printToOut(0, "", string -> sb.append(string).append('\n'));
        return sb.toString();
    }

    @Override
    public void printToOut(int level, boolean printName, String prefix, Consumer<String> out) {
        if (printName) {
            out.accept(prefix.substring(0, Math.max(0, prefix.length() - 3)) + getName());
        }

        for (String fault : faults()) {
            out.accept(prefix + "! " + fault);
        }

        for (VerificationResult<?> verificationResult : children) {
            if (verificationResult instanceof VerificationResultImpl<?> child) {
                if (child.type() == ResultType.VALID) continue;
                child.printToOut(level + 1, prefix + "   ", out);
            }
        }
    }

    public static class BuilderImpl<T> implements Builder<T> {

        private ResultType type;
        private final Verifier<T> verifier;
        private final Set<String> faults;
        private final T value;
        private final List<VerificationResult<?>> children;

        public BuilderImpl(Verifier<T> verifier, T value) {
            this.verifier = verifier;
            this.faults = new HashSet<>();
            this.value = value;
            this.children = new ArrayList<>();
        }

        @Override
        public Optional<T> currentValue() {
            return Optional.ofNullable(value);
        }

        @Override
        public ResultType currentType() {
            return type;
        }

        public Builder<T> fault(String message) {
            this.faults.add(message);
            return this;
        }

        @Override
        public Builder<T> clearFaults() {
            this.faults.clear();
            return this;
        }

        @Override
        public Builder<T> valid() {
            return type(ResultType.VALID);
        }

        @Override
        public Builder<T> invalid() {
            return type(ResultType.INVALID);
        }

        @Override
        public Builder<T> type(ResultType type) {
            this.type = type;
            return this;
        }

        public Builder<T> children(List<VerificationResult<?>> children) {
            this.children.addAll(children);
            return this;
        }

        @Override
        public VerificationResult<T> complete() {
            return new VerificationResultImpl<>(value, verifier, type, faults, children);
        }

    }

}

