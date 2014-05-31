package bot.demo.consumer.api;

import machine.lib.message.api.NetworkObject;

import java.util.List;

public interface RemoteUser extends NetworkObject<String> {

    public void doChat(String receiver, String message);

    public void doFarm(String what);

    public void doInventorize(List<String> tradeables);

    public void doLocate();

    public void doTrade(String to, List<String> tradeables);

}
