package machine.backbone.local;

import machine.management.api.entities.Server;
import machine.management.api.services.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Installer {

    private static final Logger LOG = LoggerFactory.getLogger(Installer.class);
    private static final String CONFIGURATION_DIR = System.getProperty("CONFIGURATION_DIR", "/etc/machine-backbone.conf.d");

    private Configuration configuration;

    public Installer() {
        configuration = new Configuration(CONFIGURATION_DIR);
    }

    /**
     * Installs machine-backbone, if not already installed:
     * <p/>
     * - Sets up configuration directories.
     * - Registers at the management server.
     *
     * @param defaultManagementUrl default url to the management server
     * @param port port the server is listening on
     * @return management instance
     */
    public Management install(String defaultManagementUrl, int port) {
        try {
            configuration.initialize();
            // if a local management url configuration is not available, default it
            String managementURL = configuration.getManagementURL();
            if(managementURL == null){
                managementURL = defaultManagementUrl;
                configuration.setManagementURL(managementURL);
            }
            Management management = new Management(managementURL);
            // if a local server configuration is not available, register the server
            Server server = configuration.getServer();
            if (server == null) {
                server = registerServer(management.getServerService(), port);
                configuration.setServer(server);
            }
            return management;
        } catch (Exception e) {
            LOG.error("Erred during install.", e);
            throw new ExceptionInInitializerError("Could not complete installation.");
        }
    }

    /**
     * Registers the local machine, with given port as port on the registered entity.
     *
     * @param serverService service to register at
     * @param port service port to receive backbone messages on
     * @return the created entity, with its id set
     */
    private Server registerServer(ServerService serverService, int port) {
        Server localMachine = new Server();
        localMachine.setPort(port);
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            localMachine.setHostname(hostname);
        } catch (UnknownHostException ex) {
            localMachine.setHostname(null);
        }
        return serverService.save(localMachine);
    }

}
