package bot.demo.consumer.live;

import bot.demo.consumer.live.descriptors.UserDescriptor;

public interface LiveProcess {

    public boolean login(String username, String password);

    public boolean logout(String username);

    public UserDescriptor register();

}
