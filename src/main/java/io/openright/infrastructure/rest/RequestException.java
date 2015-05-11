package io.openright.infrastructure.rest;

import java.util.function.Supplier;

public class RequestException extends RuntimeException {
    private final int statusCode;

    public RequestException(String string) {
        this(400, string);
    }

    public RequestException(int statusCode, String string) {
        super(string);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public static Supplier<RequestException> notFound(Class<?> clazz, Object id) {
        return () -> new RequestException(404, "Can't find " + clazz.getName() + " with id " + id);
    }
}
