package com.hileco.drpc.core.spec;

/**
 * {@link AutoCloseable}, however defined as to never throw an exception.
 *
 * @author Philipp Gayret
 */
public interface SilentCloseable extends AutoCloseable {

    void close();

}
