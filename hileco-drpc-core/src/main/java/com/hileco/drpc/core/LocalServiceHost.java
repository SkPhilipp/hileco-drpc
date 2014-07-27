package com.hileco.drpc.core;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.hileco.drpc.core.spec.ServiceConnector;
import com.hileco.drpc.core.spec.ServiceConnectorHost;
import com.hileco.drpc.core.spec.ServiceHost;
import com.hileco.drpc.core.util.SilentCloseable;

import java.util.Map;

/**
 * An implementation of {@link ServiceHost}, allowing only services to be registered by identifier, and accessed via connectors.
 *
 * @author Philipp Gayret
 */
public class LocalServiceHost implements ServiceConnectorHost {

    private Table<String, String, Object> servicesByClassTopic;

    public LocalServiceHost() {
        this.servicesByClassTopic = HashBasedTable.create();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ServiceConnector<T> connector(Class<T> type) {
        String topic = this.topic(type);
        Map<String, T> services = (Map<String, T>) this.servicesByClassTopic.row(topic);
        return new LocalServicesConnector<>(services);
    }

    @Override
    public <T> SilentCloseable registerService(Class<T> type, String identifier, T implementation) {
        String topic = this.topic(type);
        this.servicesByClassTopic.put(topic, identifier, implementation);
        return () -> this.servicesByClassTopic.remove(type, identifier);
    }

}
