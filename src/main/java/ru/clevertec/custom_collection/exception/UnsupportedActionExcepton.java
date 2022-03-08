package ru.clevertec.custom_collection.exception;

public class UnsupportedActionExcepton extends RuntimeException {

    public UnsupportedActionExcepton() {
        super();
    }

    public UnsupportedActionExcepton(String message) {
        super(message);
    }

    public UnsupportedActionExcepton(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedActionExcepton(Throwable cause) {
        super(cause);
    }

    protected UnsupportedActionExcepton(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
