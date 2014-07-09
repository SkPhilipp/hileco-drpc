package com.hileco.drcp.core.api.services;

import com.hileco.drcp.core.api.models.Message;
import com.hileco.drcp.core.api.exceptions.NotSubscribedException;

import java.util.UUID;

@FunctionalInterface
public interface MessageService {

    /**
     * Handles any message.
     *
     * @param subscriptionId if of the {@link com.hileco.drcp.core.api.models.Subscription} linked to this
     * @param instance message to handle
     * @throws NotSubscribedException when this service does recognize the given subscriptionId
     */
    public void handle(UUID subscriptionId, Message<?> instance) throws NotSubscribedException;

}
