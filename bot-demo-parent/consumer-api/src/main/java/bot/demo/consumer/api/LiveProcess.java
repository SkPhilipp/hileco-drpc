package bot.demo.consumer.api;

import bot.demo.consumer.api.descriptors.UserDescriptor;

public interface LiveProcess {

    public UserDescriptor login(String username, String password);

    public UserDescriptor logout(String username);

    public UserDescriptor register();

}
