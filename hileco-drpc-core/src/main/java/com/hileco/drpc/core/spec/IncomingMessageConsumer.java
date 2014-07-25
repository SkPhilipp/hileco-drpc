package com.hileco.drpc.core.spec;

import java.io.InputStream;

/**
 * Message consumer specification.
 *
 * @author Philipp Gayret
 */
@FunctionalInterface
public interface IncomingMessageConsumer {

    /**
     * Handles any message with metadata.
     *
     * @param metadata metadata describing how to handle the content
     * @param content stream to content to process
     */
    public void accept(Metadata metadata, InputStream content);

}
