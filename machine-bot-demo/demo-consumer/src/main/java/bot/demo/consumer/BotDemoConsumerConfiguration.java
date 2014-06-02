package bot.demo.consumer;

import java.util.UUID;

public class BotDemoConsumerConfiguration {

    private Integer serverPort;
    private UUID serverId;
    private String routerUrl;

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public UUID getServerId() {
        return serverId;
    }

    public void setServerId(UUID serverId) {
        this.serverId = serverId;
    }

    public String getRouterURL() {
        return routerUrl;
    }

    public void setRouterURL(String routerUrl) {
        this.routerUrl = routerUrl;
    }

}