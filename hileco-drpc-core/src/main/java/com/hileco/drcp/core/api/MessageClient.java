package com.hileco.drcp.core.api;

import com.hileco.drcp.core.api.models.Message;

import java.util.UUID;

/**
 * A basic client to the DRCP router, for "low level" communication.
 */
public interface MessageClient {

    /**
     * Publishes a message onto the network. If the message does not have an id assigned, this will assign a random id.
     *
     * @param message the message to be published
     * @return the message identifier used for publishing
     */
    public UUID publish(Message<?> message);

}
