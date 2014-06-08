package bot.demo.consumer.live;

import bot.demo.consumer.live.descriptors.UserDescriptor;
import machine.drcp.core.api.annotations.RPCTimeout;

public interface LiveProcess {

    @RPCTimeout(10)
    public boolean login(String username, String password);

    @RPCTimeout(10)
    public boolean logout(String username);

    @RPCTimeout(60)
    public UserDescriptor register();

}
