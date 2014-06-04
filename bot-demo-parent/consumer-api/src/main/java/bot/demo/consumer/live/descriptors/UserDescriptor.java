package bot.demo.consumer.live.descriptors;

import java.util.UUID;

public class UserDescriptor {

    private UUID processId;
    private String username;
    private String password;

    public UUID getProcessId() {
        return processId;
    }

    public void setProcessId(UUID processId) {
        this.processId = processId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
