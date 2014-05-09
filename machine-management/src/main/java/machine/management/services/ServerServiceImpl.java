package machine.management.services;

import com.google.common.base.Preconditions;
import machine.lib.service.dao.GenericModelDAO;
import machine.lib.service.services.AbstractQueryableModelService;
import machine.management.api.entities.Server;
import machine.management.api.services.ServerService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Path("/servers")
public class ServerServiceImpl extends AbstractQueryableModelService<Server> implements ServerService {

    public static final int DEFAULT_PORT = 80;
    private static final GenericModelDAO<Server> DAO = new GenericModelDAO<>(Server.class);
    @Context
    private HttpServletRequest request;

    public ServerServiceImpl() {
        super(DAO);
    }

    /**
     * @return the client's IP address as a string
     */
    public String getContextualRequestAddress() {
        return request.getRemoteAddr();
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Instantiates a server, ensures the IP address is not provided by the client.
     *
     * @param instance {@link Server} instance whose properties to use for instantiating the entity
     * @return the {@link UUID} assigned to the new entity
     */
    @Override
    public Server save(Server instance) {
        Preconditions.checkArgument(instance.getIpAddress() == null, "Clients are not permitted to provide the IP-address themselves.");
        if (instance.getPort() == null) {
            instance.setPort(DEFAULT_PORT);
        }
        String clientIpAddress = this.getContextualRequestAddress();
        instance.setIpAddress(clientIpAddress);
        return super.save(instance);
    }

    /**
     * Updates a servers' heartbeat date, to the local Java time.
     */
    @Override
    public void heartbeat(UUID serverId) {
        Server server = DAO.read(serverId);
        Preconditions.checkNotNull(server, "No entity found for the given Id.");
        Date now = Calendar.getInstance().getTime();
        server.setHeartbeat(now);
        DAO.save(server);
    }

}
