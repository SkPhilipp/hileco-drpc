package machine.backbone.local;

import machine.management.api.entities.Server;
import machine.management.api.services.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LocalInstaller {

    private static final Logger LOG = LoggerFactory.getLogger(LocalInstaller.class);

    private LocalConfiguration localConfiguration;

    public LocalInstaller(LocalConfiguration localConfiguration) {
        this.localConfiguration = localConfiguration;
    }

    /**
     * Installs machine-backbone, if not already installed:
     * <p/>
     * - Sets up localConfiguration directories.
     * - Registers at the management server.
     *
     * @param defaultManagementUrl default url to the management server
     * @param defaultServerPort port the server is listening on
     * @return management object
     */
    public Management install(String defaultManagementUrl, int defaultServerPort) {
        try {
            localConfiguration.initialize();
            // if a local management url localConfiguration is not available, default it
            String managementURL = localConfiguration.getManagementURL();
            if(managementURL == null){
                LOG.info("Management URL is not set, defaulting to {}", defaultManagementUrl);
                managementURL = defaultManagementUrl;
                localConfiguration.setManagementURL(managementURL);
            }
            Management management = new Management(managementURL);
            // if a local server localConfiguration is not available, register the server
            Server server = localConfiguration.getServer();
            if (server == null) {
                LOG.info("Server object not set, registering");
                server = registerServer(management.getServerService(), defaultServerPort);
                LOG.info("Registered server object with id {}", server.getId());
                localConfiguration.setServer(server);
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
