package bot.demo.messages.process;

import java.io.Serializable;

public class LogoutAction implements Serializable {

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
