package com.hileco.drcp.core.api.exceptions;

import java.util.UUID;

public class NotSubscribedException extends IllegalArgumentException {

    private UUID incomingSubscriptionId;
    private String incomingTopic;
    private UUID activeSubscriptionId;

    public NotSubscribedException(UUID incomingSubscriptionId, String incomingTopic, UUID activeSubscriptionId) {
        this.incomingSubscriptionId = incomingSubscriptionId;
        this.incomingTopic = incomingTopic;
        this.activeSubscriptionId = activeSubscriptionId;
    }

    @Override
    public String getMessage() {
        return String.format("Not subscribed to topic %s, subscription id %s, active subscription id %s", incomingTopic, incomingSubscriptionId, activeSubscriptionId);
    }

}
