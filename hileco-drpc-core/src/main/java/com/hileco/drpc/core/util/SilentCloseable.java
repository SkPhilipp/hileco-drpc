package com.hileco.drpc.core.util;

/**
 * {@link java.lang.AutoCloseable}, however defined as to never throw an exception.
 *
 * @author Philipp Gayret
 */
@FunctionalInterface
public interface SilentCloseable extends AutoCloseable {

    public static SilentCloseable many(SilentCloseable... closeables) {
        return () -> {
            for (SilentCloseable closeable : closeables) {
                closeable.close();
            }
        };
    }

    void close();

}
