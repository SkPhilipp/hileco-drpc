package com.hileco.drpc.http.subscription;

import java.util.Collection;
import java.util.UUID;

/**
 * @author Philipp Gayret
 */
public interface SubscriptionStore {

    /**
     * Timeout in milliseconds after which a subscription expires when not accessed.
     */
    public static final Long DEFAULT_TIMEOUT_MS = 30000l;

    /**
     * Saves any given subscription, assigns a random ID to it and returns.
     *
     * @param instance subscription to save
     * @return given instance with id assigned
     */
    public Subscription save(Subscription instance);

    /**
     * Resets the expire timer for a given subscription by id, essentially keeping it alive longer.
     *
     * @param id id of subscription to delete
     * @return true if a subscription was found for the given id
     */
    public boolean keepAlive(UUID id);

    /**
     * Deletes a subscription by id.
     *
     * @param id id of subscription to delete
     * @return true if a subscription was found for the given id
     */
    public boolean delete(UUID id);

    /**
     * Lists all subscriptions on a given topic.
     *
     * @param topic any message topic
     * @return list of subscriptions matching the given topic
     */
    public Collection<Subscription> withTopic(String topic);

}
