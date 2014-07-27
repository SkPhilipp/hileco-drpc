package com.hileco.drpc.core.spec;

import com.hileco.drpc.core.util.SilentCloseable;

/**
 * A more RPC oriented {@link ServiceConnectorHost}.
 *
 * @author Philipp Gayret
 */
public abstract class ServiceHost implements ServiceConnectorHost, MessageSender {

    /**
     * Class reference to use for registering callbacks via {@link #registerReceiver(Class, String, MessageReceiver)}.
     */
    public static final class Callback {}

    /**
     * Begins listening on the given type & identifier combination, any messages received on it will be delegated to the given consumer.
     *
     * @param type       any class whose name to use as topic
     * @param identifier the object identifier useable as part of the topic
     * @param consumer   handler to accept messages
     * @return the closeable useable to revert the process of this call
     * @throws IllegalArgumentException when there is an open consumer already registered on this topic
     */
    public abstract SilentCloseable registerReceiver(Class<?> type, String identifier, MessageReceiver consumer) throws IllegalArgumentException;

}
