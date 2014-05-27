package machine.humanity;

import machine.humanity.services.GeneratorServiceImpl;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.LocalServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import machine.lib.service.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class HumanityServer implements LocalServer {

    private static final Logger LOG = LoggerFactory.getLogger(HumanityServer.class);
    private HumanityConfiguration configuration;

    public HumanityServer(HumanityConfiguration configuration) {
        this.configuration = configuration;
    }

    public static void main(String[] args) throws EmbeddedServerStartException {
        HumanityConfiguration configuration = new HumanityConfiguration();
        Config.set("SERVER_PORT", 81, configuration::setServerPort);
        HumanityServer humanityServer = new HumanityServer(configuration);
        humanityServer.start();
    }

    public void start() throws EmbeddedServerStartException {

        EmbeddedServer embeddedServer = new EmbeddedServer(configuration.getServerPort());
        Set<Object> services = new HashSet<>();
        services.add(new GeneratorServiceImpl());
        embeddedServer.start(services);

    }

}
