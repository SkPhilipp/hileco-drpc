package bot.demo.messages.process;

import java.io.Serializable;
import java.util.UUID;

/**
 * Expected reply to {@link bot.demo.messages.Topic#PROCESS_SCAN}.
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
