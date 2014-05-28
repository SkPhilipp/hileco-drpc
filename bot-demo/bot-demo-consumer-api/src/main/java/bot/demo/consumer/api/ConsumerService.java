package bot.demo.consumer.api;

import machine.lib.message.api.NetworkService;

public interface ConsumerService extends NetworkService {

    void notifyScan();

}
