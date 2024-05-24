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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface VerificationResult<T> {

    List<VerificationResult<?>> children();

    boolean optional();

    String getName();

    Optional<T> value();

    ResultType type();

    Collection<String> faults();

    default void printToOut(int level, String prefix, Consumer<String> out) {
        printToOut(level, true, prefix, out);
    }

    void printToOut(int level, boolean printName, String prefix, Consumer<String> out);

    interface Builder<T> {

        Optional<T> currentValue();

        ResultType currentType();

        Builder<T> fault(String message);

        Builder<T> clearFaults();

        Builder<T> valid();

        Builder<T> invalid();

        Builder<T> type(ResultType type);

        Builder<T> children(List<VerificationResult<?>> children);

        VerificationResult<T> complete();

    }

    enum ResultType {

        UNKNOWN,
        VALID,
        INVALID;

        public ResultType and(ResultType other) {
            if (this == UNKNOWN) return other;
            return this == VALID ? other : this;
        }

        public boolean isValid() {
            return this == VALID;
        }

    }
}
