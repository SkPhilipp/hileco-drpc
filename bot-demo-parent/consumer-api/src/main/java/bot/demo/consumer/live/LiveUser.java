package bot.demo.consumer.live;

import machine.drcp.core.api.annotations.RPCTimeout;

import java.util.List;

public interface LiveUser {

    @RPCTimeout(5)
    public void chat(String receiver, String message);

    @RPCTimeout(5)
    public void farm(String what);

    @RPCTimeout(5)
    public void locate();

    @RPCTimeout(60)
    public void inventorize(List<String> tradeables);

    @RPCTimeout(60)
    public void trade(String to, List<String> tradeables);

}
