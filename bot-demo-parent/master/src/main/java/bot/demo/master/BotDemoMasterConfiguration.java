package bot.demo.master;

public class BotDemoMasterConfiguration {

    private Integer serverPort;
    private String routerUrl;
    private String humanitySource;

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getRouterUrl() {
        return routerUrl;
    }

    public void setRouterUrl(String routerUrl) {
        this.routerUrl = routerUrl;
    }

    public String getHumanitySource() {
        return humanitySource;
    }

    public void setHumanitySource(String humanitySource) {
        this.humanitySource = humanitySource;
    }

}
