package bot.demo.consumer.api;

import machine.drcp.core.api.InvokeableObject;

import java.util.UUID;

public interface RemoteProcess extends InvokeableObject<UUID> {

    public void doLogin(String username, String password);

    public void doLogout(String username);

    public void doRegister();

}
