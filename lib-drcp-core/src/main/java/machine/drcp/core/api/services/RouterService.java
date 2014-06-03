package machine.drcp.core.api.services;

import machine.drcp.core.api.models.Message;
import machine.drcp.core.api.models.Subscription;

import java.util.UUID;

public interface RouterService<T extends Subscription> {

    public int DEFAULT_SUBSCRIPTION_EXPIRE_MILLISECONDS = 5 * 60000;

    public void publish(Message message);

    /**
     * Extends a subscription duration, by id.
     *
     * @param id id of the subscription to be extended
     */
    public void extend(UUID id);

    /**
     * Instantiates a subscription, ensures the host address is not provided by the client.
     * <p>
     * When the expire date is not provided it will default to local java time + {@link #DEFAULT_SUBSCRIPTION_EXPIRE_MILLISECONDS}.
     *
     * @param instance {@link machine.drcp.core.api.models.Subscription} instance whose properties to use for instantiating the subscription
     * @return the created subscription with its id assigned
     */
    public T save(T instance);

    /**
     * Deletes an subscription by id.
     *
     * @param id id of the subscription to be deleted
     */
    public void delete(UUID id);

}
