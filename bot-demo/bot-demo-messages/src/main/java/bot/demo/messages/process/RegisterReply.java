package bot.demo.messages.process;

import java.io.Serializable;
import java.util.UUID;

public class RegisterReply implements Serializable {

    private String username;
    private UUID serverId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UUID getServerId() {
        return serverId;
    }

    public void setServerId(UUID serverId) {
        this.serverId = serverId;
    }

}
