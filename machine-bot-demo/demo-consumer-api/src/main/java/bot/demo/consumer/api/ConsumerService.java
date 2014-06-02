package bot.demo.consumer.api;

import machine.drcp.core.api.InvokeableService;

public interface ConsumerService extends InvokeableService {

    void notifyScan();

}
