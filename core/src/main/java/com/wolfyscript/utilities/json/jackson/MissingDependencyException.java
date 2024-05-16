package com.wolfyscript.utilities.json.jackson;

public class MissingDependencyException extends RuntimeException {

    public MissingDependencyException(String message) {
        super(message);
    }

    public MissingDependencyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingDependencyException(Throwable cause) {
        super(cause);
    }
}
