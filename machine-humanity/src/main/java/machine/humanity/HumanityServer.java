package machine.humanity;

import com.google.common.primitives.Ints;
import machine.humanity.services.GeneratorServiceImpl;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class HumanityServer {

    private static final Logger LOG = LoggerFactory.getLogger(HumanityServer.class);
    private HumanityConfiguration configuration;

    public HumanityServer(HumanityConfiguration configuration) {
        this.configuration = configuration;
    }

    public static void main(String[] args) throws EmbeddedServerStartException {

        Integer serverPort = Ints.tryParse(System.getProperty("SERVER_PORT", "81"));

        LOG.info("SERVER_PORT: {}", serverPort);

        HumanityConfiguration configuration = new HumanityConfiguration();
        configuration.setServerPort(serverPort);

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
