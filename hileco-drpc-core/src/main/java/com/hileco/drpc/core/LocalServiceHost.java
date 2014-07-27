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

    private Table<Class<?>, String, Object> servicesByClassTopic;

    public LocalServiceHost() {
        this.servicesByClassTopic = HashBasedTable.create();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ServiceConnector<T> connector(Class<T> type) {
        Map<String, T> services = (Map<String, T>) this.servicesByClassTopic.row(type);
        return new LocalServicesConnector<T>(services);
    }

    @Override
    public <T> SilentCloseable bind(Class<T> iface, T implementation, String identifier) {
        this.servicesByClassTopic.put(iface, identifier, implementation);
        return () -> {
            this.servicesByClassTopic.remove(iface, identifier);
        };
    }

}
