package com.hileco.drpc.http.routing.spec;

import com.hileco.drpc.core.spec.MessageClient;

import java.util.UUID;

/**
 * Functionality requirement of a router, should allow for saving subscriptions and publishing messages.
 *
 * @author Philipp Gayret
 */
public interface Router extends MessageClient {

    public int DEFAULT_SUBSCRIPTION_EXPIRE_MILLISECONDS = 5 * 60000;

    /**
     * Extends a subscription duration, by id.
     *
     * @param id id of the subscription to be extended
     */
    public void extend(UUID id);

    /**
     * Instantiates a subscription, ensures the host address is not provided by the client.
     * <p/>
     * When the expire date is not provided it will default to local java time + {@link #DEFAULT_SUBSCRIPTION_EXPIRE_MILLISECONDS}.
     *
     * @param topic      subscription topic
     * @param expireTime time in milliseconds the subscription should last
     * @return a subscription instance encapsulating the given arguments' information
     */
    public Subscription save(String topic, Long expireTime);

    /**
     * Deletes an subscription by id.
     *
     * @param id id of the subscription to be deleted
     */
    public void delete(UUID id);

}
