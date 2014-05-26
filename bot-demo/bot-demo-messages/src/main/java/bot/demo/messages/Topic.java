package bot.demo.messages;

import java.util.UUID;

public enum Topic {

    PROCESS_SCAN("scan"),
    PROCESS_SCAN_REPLY("scan-reply"),
    PROCESS_LOGIN("login"),
    PROCESS_LOGOUT("logout"),
    PROCESS_REGISTER("register"),
    USER_CHAT("chat"),
    USER_FARM("farm"),
    USER_INVENTORIZE("inventorize"),
    USER_LOCATE("locate"),
    USER_TRADE("trace");

    public static final String BOT_DEMO_PREFIX = "demo";

    private String topic;

    private Topic(String topic) {
        this.topic = topic;
    }

    public String with(UUID suffix) {
        return String.format("%s/%s/%s", BOT_DEMO_PREFIX, this.topic, suffix);
    }

    public String with(String suffix) {
        return String.format("%s/%s/%s", BOT_DEMO_PREFIX, this.topic, suffix);
    }

    @Override
    public String toString() {
        return String.format("%s/%s", BOT_DEMO_PREFIX, this.topic);
    }

}
