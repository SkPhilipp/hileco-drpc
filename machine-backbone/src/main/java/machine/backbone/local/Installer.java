package machine.backbone.local;

import machine.backbone.Management;
import machine.management.api.entities.Server;
import machine.management.api.services.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class Installer {

    private static final Logger LOG = LoggerFactory.getLogger(Installer.class);
    private static final String CONFIGURATION_DIR = System.getProperty("CONFIGURATION_DIR", "/etc/machine-backbone.conf.d");

    private static Installer INSTANCE;

    private Configuration configuration;
    private Management management;

    public Installer() {
        configuration = new Configuration(CONFIGURATION_DIR);
        management = Management.getInstance();
    }

    public static Installer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Installer();
        }
        return INSTANCE;
    }

    /**
     * Installs machine-backbone, if not already installed:
     * <p/>
     * - Sets up configuration directories.
     * - Registers at the management server.
     *
     * @param port port the server is listening on
     */
    public void install(int port) {
        try {
            configuration.initialize();
            // if a local server configuration is not available, register the server
            Server server = configuration.getServer();
            if (server == null) {
                server = registerServer(port);
                configuration.setServer(server);
            }
        } catch (Exception e) {
            LOG.error("Erred during install.", e);
            throw new ExceptionInInitializerError("Could not complete installation.");
        }
    }

    /**
     * Registers the local machine, with given port as port on the registered entity.
     *
     * @param port service port to receive backbone messages on
     * @return the created entity, with its id set
     */
    private Server registerServer(int port) {
        Server localMachine = new Server();
        localMachine.setPort(port);
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            localMachine.setHostname(hostname);
        } catch (UnknownHostException ex) {
            localMachine.setHostname(null);
        }
        ServerService serverService = management.getServerService();
        UUID id = serverService.save(localMachine);
        localMachine.setId(id);
        return localMachine;
    }

}
