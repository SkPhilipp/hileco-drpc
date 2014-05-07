package machine.management.services;

import com.google.common.base.Preconditions;
import machine.lib.service.dao.GenericModelDAO;
import machine.lib.service.services.AbstractQueryableModelService;
import machine.management.api.services.ServerService;
import machine.management.api.entities.Server;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.UUID;

@Path("/servers")
public class ServerServiceImpl extends AbstractQueryableModelService<Server> implements ServerService {

    private static final GenericModelDAO<Server> DAO = new GenericModelDAO<>(Server.class);

    public static final int DEFAULT_PORT = 80;

    private HttpServletRequest request;

    public ServerServiceImpl(@Context HttpServletRequest request) {
        super(DAO);
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
