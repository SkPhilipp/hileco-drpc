package bot.demo.consumer.api;

import bot.demo.master.api.RemoteMaster;
import machine.lib.message.api.NetworkConnector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RemoteProcessImpl implements RemoteProcess, AutoCloseable {

    private final NetworkConnector networkConnector;
    private final UUID processId;
    private final Map<String, RemoteUserImpl> userActionHandlers;
    private final RemoteMaster remoteMaster;

    public RemoteProcessImpl(UUID processId, NetworkConnector networkConnector) {
        this.networkConnector = networkConnector;
        this.userActionHandlers = new HashMap<>();
        this.processId = processId;
        this.remoteMaster = this.networkConnector.remote(RemoteMaster.class);
    }

    public void start() {
        this.networkConnector.listen(RemoteProcess.class, this, processId);
    }

    @Override
    public void close() {
        this.networkConnector.endListen(this, processId);
    }

    @Override
    public void doLogin(String username, String password) {
        if (!this.userActionHandlers.containsKey(username)) {
            RemoteUserImpl remoteUserImpl = this.userActionHandlers.get(username);
            remoteUserImpl.close();
            this.userActionHandlers.remove(username);
            this.remoteMaster.completedLogin(this.processId, username);
        }
    }

    @Override
    public void doLogout(String username) {
        if (this.userActionHandlers.containsKey(username)) {
            RemoteUserImpl remoteUserImpl = this.userActionHandlers.get(username);
            remoteUserImpl.close();
            this.userActionHandlers.remove(username);
            this.remoteMaster.completedLogout(this.processId, username);
        }
    }

    @Override
    public void doRegister() {
        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        RemoteUserImpl remoteUserImpl = new RemoteUserImpl(username, this.networkConnector);
        remoteUserImpl.start();
        this.userActionHandlers.put(username, remoteUserImpl);
        this.remoteMaster.completedRegister(this.processId, username, password);
    }

}
