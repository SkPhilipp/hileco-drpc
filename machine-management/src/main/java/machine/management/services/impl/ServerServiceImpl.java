package machine.management.services.impl;

import com.google.common.base.Preconditions;
import machine.management.model.Server;
import machine.management.services.ServerService;

import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

@Path("/servers")
public class ServerServiceImpl extends AbstractModelService<Server> implements ServerService {

    public static final int DEFAULT_PORT = 80;

    @Override
    public UUID create(Server instance){
        Preconditions.checkArgument(instance.getIpAddress() == null, "Clients are not permitted to provide the IP-address themselves.");
        if(instance.getPort() == null){
            instance.setPort(DEFAULT_PORT);
        }
        String address = null; // TODO: resolve client ip-address, trust proxies
        instance.setIpAddress(address);
        return super.create(instance);
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    @Override
    public List<Server> findAll() {
        Query query = this.getEntityManager().createNativeQuery("SELECT * FROM Server");
        return query.getResultList();
    }

}
