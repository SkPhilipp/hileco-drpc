package machine.management.services;

import com.google.common.base.Preconditions;
import machine.management.model.Server;
import machine.management.services.generic.AbstractModelService;

import java.util.UUID;

public class ServerServiceImpl extends AbstractModelService<Server> implements ServerService {

    public static final int DEFAULT_PORT = 80;

    /**
     * Instantiates a server, ensures the IP address is not provided by the client.
     *
     * @param instance {@link Server} instance whose properties to use for instantiating the entity
     * @return the {@link UUID} assigned to the new entity
     */
    @Override
    public UUID create(Server instance) {
        Preconditions.checkArgument(instance.getIpAddress() == null, "Clients are not permitted to provide the IP-address themselves.");
        if (instance.getPort() == null) {
            instance.setPort(DEFAULT_PORT);
        }
        // TODO: resolve client ip-address, trust proxies
        String address = null;
        instance.setIpAddress(address);
        return super.create(instance);
    }

}
