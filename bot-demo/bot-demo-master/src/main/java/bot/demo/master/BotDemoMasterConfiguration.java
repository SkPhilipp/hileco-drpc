package bot.demo.master;

public class BotDemoMasterConfiguration {

    private Integer serverPort;
    private String managementUrl;
    private String humanityUrl;

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getManagementUrl() {
        return managementUrl;
    }

    public void setManagementUrl(String managementUrl) {
        this.managementUrl = managementUrl;
    }

    public String getHumanityUrl() {
        return humanityUrl;
    }

    public void setHumanityUrl(String humanityUrl) {
        this.humanityUrl = humanityUrl;
    }

}
