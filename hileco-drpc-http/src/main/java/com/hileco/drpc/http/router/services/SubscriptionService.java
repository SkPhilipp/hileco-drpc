package com.hileco.drpc.http.router.services;

import com.hileco.drpc.http.subscription.Subscription;

import java.util.UUID;

/**
 * Functionality of {@link com.hileco.drpc.http.subscription.SubscriptionStore} available as router-service.
 *
 * @author Philipp Gayret
 */
public interface SubscriptionService {

    /**
     * @see com.hileco.drpc.http.subscription.SubscriptionStore#save(com.hileco.drpc.http.subscription.Subscription)
     */
    public Subscription save(String topic, String address);

    /**
     * @see com.hileco.drpc.http.subscription.SubscriptionStore#keepAlive(java.util.UUID)
     */
    public boolean keepAlive(UUID id);

    /**
     * @see com.hileco.drpc.http.subscription.SubscriptionStore#delete(java.util.UUID)
     */
    public boolean delete(UUID id);

}
