package machine.services;

import com.google.common.base.Preconditions;
import machine.management.model.Server;
import machine.services.lib.services.AbstractQueryableModelService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.UUID;

@Path("/servers")
public class ServerServiceImpl extends AbstractQueryableModelService<Server> {

    public static final int DEFAULT_PORT = 80;

    private HttpServletRequest request;

    public ServerServiceImpl(@Context HttpServletRequest request) {
        this.request = request;
    }

    /**
     * @return the client's IP address as a string
     */
    public String getContextualRequestAddress() {
        return request.getRemoteAddr();
    }

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
        String clientIpAddress = this.getContextualRequestAddress();
        instance.setIpAddress(clientIpAddress);
        return super.create(instance);
    }

}
