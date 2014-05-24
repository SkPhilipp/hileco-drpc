package machine.backbone;

public class BackboneConfiguration {

    private String configurationDir;
    private String defaultManagementUrl;
    private Integer defaultServerPort;
    private Integer defaultHeartbeatPeriod;

    public String getConfigurationDir() {
        return configurationDir;
    }

    public void setConfigurationDir(String configurationDir) {
        this.configurationDir = configurationDir;
    }

    public String getDefaultManagementUrl() {
        return defaultManagementUrl;
    }

    public void setDefaultManagementUrl(String defaultManagementUrl) {
        this.defaultManagementUrl = defaultManagementUrl;
    }

    public Integer getDefaultServerPort() {
        return defaultServerPort;
    }

    public void setDefaultServerPort(Integer defaultServerPort) {
        this.defaultServerPort = defaultServerPort;
    }

    public Integer getDefaultHeartbeatPeriod() {
        return defaultHeartbeatPeriod;
    }

    public void setDefaultHeartbeatPeriod(Integer defaultHeartbeatPeriod) {
        this.defaultHeartbeatPeriod = defaultHeartbeatPeriod;
    }


}
