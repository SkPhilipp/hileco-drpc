package bot.demo.master.api;

import machine.lib.message.api.NetworkService;

import java.util.Collection;
import java.util.UUID;

public interface MasterService extends NetworkService {

    public void completedLogin(UUID processId, String username);

    public void completedLogout(UUID processId, String username);

    public void completedRegister(UUID processId, String username, String password);

    public void completedScan(UUID processId, Integer slots, Collection<String> usernames);

}
