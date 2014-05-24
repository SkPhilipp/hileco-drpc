package machine.backbone.services;

import machine.backbone.local.LocalConfiguration;
import machine.backbone.local.Management;
import machine.management.api.services.RemoteManagementService;

import java.util.UUID;

/**
 * Implementation of {@link machine.management.api.services.RemoteManagementService}
 */
public class RemoteManagementImpl implements RemoteManagementService {

    private final Management management;
    private final LocalConfiguration localConfiguration;

    public RemoteManagementImpl(Management management, LocalConfiguration localConfiguration) {
        this.management = management;
        this.localConfiguration = localConfiguration;
    }

    @Override
    public UUID getServerId() {
        return this.localConfiguration.getServer().getId();
    }

    @Override
    public String getManagementURL() {
        return this.localConfiguration.getManagementURL();
    }

    @Override
    public void setManagementURL(String managementURL) {
        this.management.setURL(managementURL);
        this.localConfiguration.setManagementURL(managementURL);
    }

}
