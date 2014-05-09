package machine.backbone;

import com.google.common.primitives.Ints;
import machine.backbone.local.Configuration;
import machine.backbone.local.Installer;
import machine.backbone.local.Management;
import machine.backbone.processes.HeartbeatProcess;
import machine.backbone.services.RemoteCommandServiceImpl;
import machine.backbone.services.RemoteManagementImpl;
import machine.lib.service.EmbeddedServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static final String CONFIGURATION_DIR = System.getProperty("CONFIGURATION_DIR", "/etc/machine-backbone.conf.d");
    public static final String MANAGEMENT_URL = System.getProperty("MANAGEMENT_URL", "http://localhost:80/");
    public static final Integer SERVER_PORT = Ints.tryParse(System.getProperty("SERVER_PORT", "82"));
    public static final Integer HEARTBEAT_PERIOD = Ints.tryParse(System.getProperty("HEARTBEAT_PERIOD", "10"));

    public static void main(String[] args) throws Exception {

        LOG.info("Initializing with:");
        LOG.info("- CONFIGURATION_DIR: {}", CONFIGURATION_DIR);
        LOG.info("- MANAGEMENT_URL: {}", MANAGEMENT_URL);
        LOG.info("- SERVER_PORT: {}", SERVER_PORT);
        LOG.info("- HEARTBEAT_PERIOD: {}", HEARTBEAT_PERIOD);

        // perform installation
        Configuration configuration = new Configuration(CONFIGURATION_DIR);
        Installer installer = new Installer(configuration);
        Management management = installer.install(MANAGEMENT_URL, SERVER_PORT);

        // run the server
        EmbeddedServer embeddedServer = new EmbeddedServer(SERVER_PORT);
        Set<Object> services = new HashSet<>();
        services.add(new RemoteCommandServiceImpl());
        services.add(new RemoteManagementImpl(management, configuration));
        embeddedServer.start(services);

        // begin processes
        HeartbeatProcess heartbeatProcess = new HeartbeatProcess(management.getServerService(), configuration);
        heartbeatProcess.schedule(HEARTBEAT_PERIOD);

    }

}
