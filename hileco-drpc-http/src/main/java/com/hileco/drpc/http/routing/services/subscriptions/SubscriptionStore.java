package com.hileco.drpc.http.routing.services.subscriptions;

import java.util.Collection;
import java.util.UUID;

/**
 * Subscription store spec, defines basic CRUD functionality.
 *
 * @author Philipp Gayret
 */
public interface SubscriptionStore {

    public Subscription read(UUID id);

    public Subscription save(Subscription instance);

    public void delete(UUID id);

    public Collection<Subscription> withTopic(String topic);

}
