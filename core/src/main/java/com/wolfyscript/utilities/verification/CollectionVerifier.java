package com.wolfyscript.utilities.verification;

import java.util.Collection;

public interface CollectionVerifier<T> extends Verifier<Collection<T>> {

    Verifier<T> getElementVerifier();

}
