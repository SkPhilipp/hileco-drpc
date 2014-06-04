package bot.demo.consumer.live;

import bot.demo.consumer.live.descriptors.ProcessDescriptor;
import bot.demo.consumer.live.descriptors.UserDescriptor;
import machine.drcp.core.api.Client;
import machine.drcp.core.api.util.Listener;
import machine.drcp.core.api.util.SilentCloseable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// TODO: convert from remoteMaster with global messages to report-callback style interface
public class Process extends Listener implements GlobalConsumer, LiveProcess {

    private static final Integer MAXIMUM_USERS_PER_CONSUMER = 5;

    private final Client client;
    private final UUID processId;
    private final Map<String, User> localUsers;

    public Process(UUID processId, Client client) {
        this.processId = processId;
        this.client = client;
        this.localUsers = new HashMap<>();
    }

    @Override
    protected SilentCloseable listen() {
        SilentCloseable processListener = this.client.listen(LiveProcess.class, this, processId);
        SilentCloseable globalListener = this.client.listen(GlobalConsumer.class, this);
        return SilentCloseable.many(globalListener, processListener);
    }

    @Override
    public ProcessDescriptor scan() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setProcessId(this.processId);
        descriptor.setSlots(MAXIMUM_USERS_PER_CONSUMER - localUsers.size());
        descriptor.setUsernames(this.localUsers.keySet());
        return descriptor;
    }

    @Override
    public void login(String username, String password) {
        if (!this.localUsers.containsKey(username) && this.localUsers.size() < MAXIMUM_USERS_PER_CONSUMER) {
            User user = new User(username, password, client);
            user.start();
            this.localUsers.put(username, user);
        }
        // TODO: Add status code as result
    }

    @Override
    public void logout(String username) {
        if (this.localUsers.containsKey(username)) {
            User user = this.localUsers.get(username);
            user.close();
            this.localUsers.remove(username);
        }
        // TODO: Add status code as result
    }

    @Override
    public UserDescriptor register() {
        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        User user = new User(username, password, this.client);
        user.start();
        this.localUsers.put(username, user);
        // TODO: Add notification utilities as with promises to report back before call is completed
        UserDescriptor userDescriptor = new UserDescriptor();
        userDescriptor.setUsername(username);
        userDescriptor.setPassword(password);
        userDescriptor.setProcessId(this.processId);
        return userDescriptor;
    }

}
