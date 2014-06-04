package machine.drcp.core.api.util;

/**
 * {@link java.lang.AutoCloseable}, that never throws an exception.
 */
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
