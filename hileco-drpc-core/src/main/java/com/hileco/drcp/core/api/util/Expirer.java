package com.hileco.drcp.core.api.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Expirer {

    private ScheduledExecutorService executorService;

    public Expirer(int poolsize) {
        this.executorService = Executors.newScheduledThreadPool(poolsize);
    }

    /**
     * Schedules a SilentCloseable for closing, returns a wrapper lambda that ensures the given closeable
     * does not close twice.
     *
     * @param closeable closeable to close automatically
     * @param time      timeout time
     * @param timeUnit  given time's {@link java.util.concurrent.TimeUnit}
     * @return a wrapper which only closes the actual closeable once
     */
    public SilentCloseable schedule(SilentCloseable closeable, int time, TimeUnit timeUnit) {
        Referenced<Boolean> closed = new Referenced<>(false);
        SilentCloseable onceCloseable = () -> {
            if (!closed.value(true)) {
                closeable.close();
            }
        };
        executorService.schedule(onceCloseable::close, time, timeUnit);
        return onceCloseable;
    }

}
