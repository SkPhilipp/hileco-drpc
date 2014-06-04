package bot.demo.master.api.live;

import bot.demo.consumer.live.LiveUser;

public class RemoteLiveUser {

    private final String username;
    private final LiveUser liveUser;

    public RemoteLiveUser(String username, LiveUser liveUser) {
        this.username = username;
        this.liveUser = liveUser;
    }

    public String getUsername() {
        return username;
    }

    public LiveUser getLive() {
        return liveUser;
    }

}
