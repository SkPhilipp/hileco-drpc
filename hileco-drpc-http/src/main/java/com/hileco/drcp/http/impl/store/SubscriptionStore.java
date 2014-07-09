package com.hileco.drcp.http.impl.store;

import com.hileco.drcp.http.api.models.HTTPSubscription;

import java.util.Collection;
import java.util.UUID;

public interface SubscriptionStore {

    public HTTPSubscription read(UUID id);

    public HTTPSubscription save(HTTPSubscription instance);

    public void delete(UUID id);

    public Collection<HTTPSubscription> withTopic(String topic);

}
