package bot.demo.consumer.api;

import machine.lib.message.api.InvokeableService;

public interface ConsumerService extends InvokeableService {

    void notifyScan();

}
