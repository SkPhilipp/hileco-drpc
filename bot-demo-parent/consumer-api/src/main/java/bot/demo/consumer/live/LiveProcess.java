package bot.demo.consumer.live;

import bot.demo.consumer.live.descriptors.UserDescriptor;

public interface LiveProcess {

    public void login(String username, String password);

    public void logout(String username);

    public UserDescriptor register();

}
