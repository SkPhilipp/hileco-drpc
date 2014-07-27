package com.hileco.drpc.core.util;

/**
 * {@link java.lang.AutoCloseable}, however defined as to never throw an exception.
 *
 * @author Philipp Gayret
 */
@FunctionalInterface
public interface SilentCloseable extends AutoCloseable {

    /**
     * A {@link SilentCloseable} which literally does nothing.
     */
    public static final SilentCloseable NULL = () -> {};

    public static SilentCloseable many(SilentCloseable... closeables) {
        return () -> {
            for (SilentCloseable closeable : closeables) {
                closeable.close();
            }
        };
    }

    void close();

}
