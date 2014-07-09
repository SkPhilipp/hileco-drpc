package com.hileco.drcp.core.api.util;

/**
 * A reference to anything.
 */
public class Referenced<T> {

    private T value;

    public Referenced(T value) {
        this.value = value;
    }

    public T value() {
        return value;
    }

    public synchronized T value(T value) {
        T result = this.value;
        this.value = value;
        return result;
    }

}
