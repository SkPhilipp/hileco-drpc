package machine.backbone.services;

import machine.backbone.local.Configuration;
import machine.backbone.local.Management;
import machine.management.api.services.RemoteManagementService;

import java.util.UUID;

/**
 * Implementation of {@link machine.management.api.services.RemoteManagementService}
 */
public class RemoteManagementImpl implements RemoteManagementService {

    private final Management management;
    private final Configuration configuration;

    public RemoteManagementImpl(Management management, Configuration configuration) {
        this.management = management;
        this.configuration = configuration;
    }

    @Override
    public UUID getServerId() {
        return this.configuration.getServer().getId();
    }

    @Override
    public String getManagementURL() {
        return this.configuration.getManagementURL();
    }

    @Override
    public void setManagementURL(String managementURL) {
        this.management.setURL(managementURL);
        this.configuration.setManagementURL(managementURL);
    }

}
