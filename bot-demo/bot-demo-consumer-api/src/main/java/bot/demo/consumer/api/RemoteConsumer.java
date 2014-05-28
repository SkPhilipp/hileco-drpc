package bot.demo.consumer.api;

import machine.lib.message.proxy.Remote;

public interface RemoteConsumer extends Remote {

    void notifyScan();

}
