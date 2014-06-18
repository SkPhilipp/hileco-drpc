package machine.lib.service;

import machine.lib.service.exceptions.EmbeddedServerStartException;

public interface LocalServer {

    public void start() throws EmbeddedServerStartException;

}
