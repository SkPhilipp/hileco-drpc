package bot.demo.consumer;

public class BotDemoConsumerConfiguration {

    private Integer serverPort;
    private String backboneUrl;

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getBackboneUrl() {
        return backboneUrl;
    }

    public void setBackboneUrl(String backboneUrl) {
        this.backboneUrl = backboneUrl;
    }

}
