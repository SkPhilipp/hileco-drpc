package com.hileco.drpc.http.router.subscription;

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

    public boolean extend(UUID id);

    public boolean delete(UUID id);

    public Collection<Subscription> withTopic(String topic);

}
