package com.hileco.drpc.core.spec;

/**
 * Allows message publishing of any kind.
 *
 * @author Philipp Gayret
 */
public interface MessageSender {

    /**
     * Publishes a message onto the network. If the metadata does not have its id assigned, this sets the id field of the given metadata.
     *
     * @param metadata the message's metadata to publish
     * @param content the content to publish
     */
    public void send(Metadata metadata, Object[] content);

}
