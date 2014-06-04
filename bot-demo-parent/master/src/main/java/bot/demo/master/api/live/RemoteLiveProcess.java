package bot.demo.master.api.live;

import bot.demo.consumer.live.LiveProcess;

import java.util.UUID;

public class RemoteLiveProcess {

    private final UUID id;
    private final LiveProcess liveProcess;
    private Integer slots;

    public RemoteLiveProcess(UUID id, LiveProcess liveProcess, Integer slots) {
        this.id = id;
        this.liveProcess = liveProcess;
        this.slots = slots;
    }

    public UUID getId() {
        return id;
    }

    public LiveProcess getLive() {
        return liveProcess;
    }

    public Integer getSlots() {
        return slots;
    }

    public void setSlots(Integer slots) {
        this.slots = slots;
    }

}
