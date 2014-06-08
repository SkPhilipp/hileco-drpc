package bot.demo.consumer.live;

import bot.demo.consumer.live.descriptors.ProcessDescriptor;
import machine.drcp.core.api.annotations.RPCTimeout;

public interface GlobalConsumer {

    @RPCTimeout(5)
    public ProcessDescriptor scan();

}
