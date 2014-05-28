package bot.demo.consumer.api;

import machine.lib.message.proxy.BoundRemote;

import java.util.UUID;

public interface RemoteProcess extends BoundRemote<UUID> {

    public void doLogin(String username, String password);

    public void doLogout(String username);

    public void doRegister();

}
