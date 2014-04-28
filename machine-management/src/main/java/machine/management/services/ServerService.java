package machine.management.services;

import machine.management.model.Server;

import java.util.List;

public interface ServerService extends ModelService<Server> {

    /**
     * Lists the full set of servers.
     *
     * @return all servers
     */
    public List<Server> findAll();

}
