package bot.demo.messages;

import java.util.UUID;

/**
 * Expected reply to {@link Topics#SCAN}.
 */
public class ScanReply {

    private UUID serverId;

    public UUID getServerId() {
        return serverId;
    }

    public void setServerId(UUID serverId) {
        this.serverId = serverId;
    }

}
