package bot.demo.consumer.api;

import bot.demo.master.api.MasterService;
import machine.lib.message.api.NetworkConnector;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ConsumerServiceImpl implements ConsumerService, RemoteProcess, AutoCloseable {

    private static final Integer INITIAL_SLOTS_SIZE = 5;

    private final NetworkConnector networkConnector;
    private final MasterService remoteMaster;
    private final UUID processId;
    private final Map<String, UserImpl> localUsers;

    public ConsumerServiceImpl(UUID processId, NetworkConnector networkConnector) {
        this.processId = processId;
        this.networkConnector = networkConnector;
        this.remoteMaster = this.networkConnector.remote(MasterService.class);
        this.localUsers = new HashMap<>();
    }

    public void start() {
        this.networkConnector.listen(ConsumerService.class, this);
        this.networkConnector.listen(RemoteProcess.class, this, processId);
    }

    @Override
    public void close() throws Exception {
        this.networkConnector.endListen(ConsumerService.class, this);
        this.networkConnector.endListen(RemoteProcess.class, this, processId);
    }

    @Override
    public void notifyScan() {
        Set<String> usernames = localUsers.keySet();
        this.remoteMaster.completedScan(processId, INITIAL_SLOTS_SIZE - localUsers.size(), usernames);
    }

    @Override
    public void doLogin(String username, String password) {
        if (!this.localUsers.containsKey(username)) {
            UserImpl userImpl = new UserImpl(username, networkConnector);
            userImpl.start();
            this.localUsers.remove(username);
            this.remoteMaster.completedLogin(this.processId, username);
        }
    }

    @Override
    public void doLogout(String username) {
        if (this.localUsers.containsKey(username)) {
            UserImpl userImpl = this.localUsers.get(username);
            userImpl.close();
            this.localUsers.remove(username);
            this.remoteMaster.completedLogout(this.processId, username);
        }
    }

    @Override
    public void doRegister() {
        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        UserImpl userImpl = new UserImpl(username, this.networkConnector);
        userImpl.start();
        this.localUsers.put(username, userImpl);
        this.remoteMaster.completedRegister(this.processId, username, password);
    }

}
