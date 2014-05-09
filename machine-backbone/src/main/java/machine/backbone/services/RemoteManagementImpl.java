package machine.backbone.services;

import machine.backbone.local.Configuration;
import machine.backbone.local.Management;
import machine.management.api.services.RemoteManagementService;

import java.io.IOException;
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
        try {
            return this.configuration.getServer().getId();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read a configuration file.", e);
        }
    }

    @Override
    public String getManagementURL() {
        try {
            return this.configuration.getManagementURL();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read a configuration file.", e);
        }
    }

    @Override
    public void setManagementURL(String managementURL) {
        try {
            this.management.setURL(managementURL);
            this.configuration.setManagementURL(managementURL);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write a configuration file.", e);
        }
    }

}
