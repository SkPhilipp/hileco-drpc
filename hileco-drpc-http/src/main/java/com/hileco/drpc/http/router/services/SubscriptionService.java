package com.hileco.drpc.http.router.services;

import com.hileco.drpc.http.router.subscription.Subscription;

import java.util.Collection;
import java.util.UUID;

/**
 * @author Philipp Gayret
 */
public interface SubscriptionService {

    public Subscription save(String topic, String address);

    public boolean extend(UUID id);

    public boolean delete(UUID id);

    public Collection<Subscription> withTopic(String topic);

}
