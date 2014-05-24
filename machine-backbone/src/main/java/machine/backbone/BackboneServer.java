package machine.backbone;

import com.google.common.primitives.Ints;
import machine.backbone.local.LocalInstaller;
import machine.backbone.local.LocalConfiguration;
import machine.backbone.local.Management;
import machine.backbone.processes.HeartbeatProcess;
import machine.backbone.services.RemoteCommandServiceImpl;
import machine.backbone.services.RemoteManagementImpl;
import machine.lib.service.EmbeddedServer;
import machine.lib.service.exceptions.EmbeddedServerStartException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class BackboneServer {

    private static final Logger LOG = LoggerFactory.getLogger(BackboneServer.class);
    private final BackboneConfiguration configuration;

    public BackboneServer(BackboneConfiguration configuration) {
        this.configuration = configuration;
    }

    public static void main(String[] args) throws EmbeddedServerStartException {

        String configurationDir = System.getProperty("CONFIGURATION_DIR", "/etc/machine-backbone.conf.d");
        String managementUrl = System.getProperty("MANAGEMENT_URL", "http://localhost:80/");
        Integer serverPort = Ints.tryParse(System.getProperty("SERVER_PORT", "82"));
        Integer heartbeatPeriod = Ints.tryParse(System.getProperty("HEARTBEAT_PERIOD", "10"));

        LOG.info("CONFIGURATION_DIR: {}", configurationDir);
        LOG.info("MANAGEMENT_URL: {}", managementUrl);
        LOG.info("SERVER_PORT: {}", serverPort);
        LOG.info("HEARTBEAT_PERIOD: {}", heartbeatPeriod);

        BackboneConfiguration backboneConfiguration = new BackboneConfiguration();
        backboneConfiguration.setConfigurationDir(configurationDir);
        backboneConfiguration.setDefaultManagementUrl(managementUrl);
        backboneConfiguration.setDefaultServerPort(serverPort);
        backboneConfiguration.setDefaultHeartbeatPeriod(heartbeatPeriod);

        BackboneServer backboneServer = new BackboneServer(backboneConfiguration);
        backboneServer.start();

    }

    public void start() throws EmbeddedServerStartException {

        LocalConfiguration localConfiguration = new LocalConfiguration(configuration.getConfigurationDir());
        LocalInstaller localInstaller = new LocalInstaller(localConfiguration);
        Management management = localInstaller.install(configuration.getDefaultManagementUrl(), configuration.getDefaultServerPort());

        EmbeddedServer embeddedServer = new EmbeddedServer(configuration.getDefaultServerPort());
        Set<Object> services = new HashSet<>();
        services.add(new RemoteCommandServiceImpl());
        services.add(new RemoteManagementImpl(management, localConfiguration));
        embeddedServer.start(services);

        HeartbeatProcess heartbeatProcess = new HeartbeatProcess(management.getServerService(), localConfiguration);
        heartbeatProcess.schedule(configuration.getDefaultHeartbeatPeriod());

    }

}
