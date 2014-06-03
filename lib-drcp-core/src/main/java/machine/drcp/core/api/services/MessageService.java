package machine.drcp.core.api.services;

import machine.drcp.core.api.models.Message;
import machine.drcp.core.api.exceptions.NotSubscribedException;

import java.util.UUID;

public interface MessageService {

    /**
     * Handles any message.
     *
     * @param subscriptionId if of the {@link machine.drcp.core.api.models.Subscription} linked to this
     * @param instance message to handle
     * @throws NotSubscribedException when this service does recognize the given subscriptionId
     */
    public void handle(UUID subscriptionId, Message<?> instance) throws NotSubscribedException;

}
