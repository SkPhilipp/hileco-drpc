package bot.demo.master.api;

import bot.demo.consumer.api.RemoteConsumer;
import bot.demo.consumer.api.RemoteProcess;
import bot.demo.consumer.api.RemoteUser;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import machine.lib.message.api.NetworkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class RemoteMasterImpl implements RemoteMaster, AutoCloseable {

    public static final int SCAN_RATE = 10;
    private static final Logger LOG = LoggerFactory.getLogger(RemoteMasterImpl.class);
    private final ScheduledExecutorService scheduler;
    private final Cache<UUID, RemoteProcess> processCache;
    private final Cache<String, RemoteUser> userCache;
    private final Function<UUID, RemoteProcess> remoteProcessFunction;
    private final Function<String, RemoteUser> remoteUserFunction;
    private final RemoteConsumer remoteConsumer;
    private NetworkConnector networkConnector;

    public RemoteMasterImpl(NetworkConnector networkConnector) {
        this.networkConnector = networkConnector;
        this.remoteConsumer = this.networkConnector.remote(RemoteConsumer.class);
        this.remoteUserFunction = this.networkConnector.remoteBound(RemoteUser.class);
        this.remoteProcessFunction = this.networkConnector.remoteBound(RemoteProcess.class);
        this.processCache = CacheBuilder.newBuilder().expireAfterWrite(SCAN_RATE * 2, TimeUnit.SECONDS).build();
        // TODO: build user scan
        this.userCache = CacheBuilder.newBuilder().expireAfterWrite(SCAN_RATE * 2, TimeUnit.SECONDS).build();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Publishes this {@link RemoteMaster} implementation on the network, initiates scanning.
     */
    public void start() {
        networkConnector.listen(RemoteMaster.class, this);
        scheduler.scheduleAtFixedRate(remoteConsumer::notifyScan, 0, SCAN_RATE, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(() -> {
            LOG.info("Stats - Processes: {}, Users: {}", processCache.size(), userCache.size());
        }, 1, SCAN_RATE, TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        networkConnector.endListen(this);
    }

    @Override
    public void completedLogin(UUID processId, String username) {
        try {
            LOG.info("completed login, processId = {}, username = {}", processId, username);
            userCache.get(username, () -> remoteUserFunction.apply(username));
        } catch (ExecutionException e) {
            LOG.error("Erred while creating a RemoteUser object", e);
        }
    }

    @Override
    public void completedLogout(UUID processId, String username) {
        LOG.info("completed logout, processId = {}, username = {}", processId, username);
        userCache.invalidate(username);
    }

    @Override
    public void completedRegister(UUID processId, String username, String password) {
        try {
            LOG.info("completed register, processId = {}, username = {}, password = {}", processId, username, password);
            userCache.get(username, () -> remoteUserFunction.apply(username));
        } catch (ExecutionException e) {
            LOG.error("Erred while creating a RemoteUser object", e);
        }
    }

    @Override
    public void completedScan(UUID processId) {
        try {
            LOG.info("completed scan, processId = {}", processId);
            processCache.get(processId, () -> remoteProcessFunction.apply(processId));
        } catch (ExecutionException e) {
            LOG.error("Erred while creating a RemoteProcess object", e);
        }
    }

}
