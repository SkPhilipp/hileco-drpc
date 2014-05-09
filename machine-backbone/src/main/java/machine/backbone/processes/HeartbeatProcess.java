package machine.backbone.processes;

import machine.management.api.services.ServerService;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartbeatProcess extends TimerTask {

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    private ServerService serverService;

    public HeartbeatProcess(ServerService serverService) {
        this.serverService = serverService;
    }

    /**
     * Schedules the heartbeat process to run.
     *
     * @param period the period between successive executions in seconds
     */
    public void schedule(long period){
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(this, 0, period, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        this.serverService.heartbeat();
    }

}
