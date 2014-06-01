package bot.demo.master.api.live;

import bot.demo.consumer.api.RemoteProcess;

import java.util.UUID;

public class LiveProcess {

    private final UUID id;
    private final RemoteProcess remoteProcess;
    private Integer slots;

    public LiveProcess(UUID id, RemoteProcess remoteProcess, Integer slots) {
        this.id = id;
        this.remoteProcess = remoteProcess;
        this.slots = slots;
    }

    public UUID getId() {
        return id;
    }

    public RemoteProcess getRemoteProcess() {
        return remoteProcess;
    }

    public Integer getSlots() {
        return slots;
    }

    public void setSlots(Integer slots) {
        this.slots = slots;
    }

}
