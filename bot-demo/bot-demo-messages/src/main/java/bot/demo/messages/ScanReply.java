package bot.demo.messages;

import java.io.Serializable;
import java.util.UUID;

/**
 * Expected reply to {@link Topics#SCAN}.
 */
public class ScanReply implements Serializable {

    private UUID serverId;

    public UUID getServerId() {
        return serverId;
    }

    public void setServerId(UUID serverId) {
        this.serverId = serverId;
    }

}
