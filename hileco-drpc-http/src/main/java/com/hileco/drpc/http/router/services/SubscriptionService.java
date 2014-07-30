package com.hileco.drpc.http.router.services;

import java.util.Collection;
import java.util.UUID;

/**
 * @author Philipp Gayret
 */
public interface SubscriptionService {

    public Subscription save(String topic, String host, Integer port);

    public void extend(UUID id);

    public void delete(UUID id);

    public Collection<Subscription> withTopic(String topic);

}
