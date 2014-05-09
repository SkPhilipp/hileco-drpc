package machine.backbone.processes;

import com.google.common.base.Preconditions;
import machine.backbone.local.Configuration;
import machine.management.api.entities.Server;
import machine.management.api.services.ServerService;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartbeatProcess extends TimerTask {

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    private ServerService serverService;
    private Configuration configuration;

    public HeartbeatProcess(ServerService serverService, Configuration configuration) {
        this.serverService = serverService;
        this.configuration = configuration;
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
        Server local = configuration.getServer();
        Preconditions.checkNotNull(local, "Local server entity is required for heartbeat process.");
        this.serverService.heartbeat(local.getId());
    }

}
