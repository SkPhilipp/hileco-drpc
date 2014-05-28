package bot.demo.master.api.live;

import bot.demo.consumer.api.RemoteUser;

public class LiveUser {

    private final String username;
    private final RemoteUser remoteUser;

    public LiveUser(String username, RemoteUser remoteUser) {
        this.username = username;
        this.remoteUser = remoteUser;
    }

    public String getUsername() {
        return username;
    }

    public RemoteUser getRemoteUser() {
        return remoteUser;
    }

}
