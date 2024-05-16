package com.wolfyscript.utilities.json.jackson;

public class MissingImplementationException extends RuntimeException {

    public MissingImplementationException(String message) {
        super(message);
    }

    public MissingImplementationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingImplementationException(Throwable cause) {
        super(cause);
    }
}
