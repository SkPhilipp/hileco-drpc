package bot.demo.master.api;

import bot.demo.consumer.api.ConsumerService;
import bot.demo.consumer.api.RemoteProcess;
import bot.demo.consumer.api.RemoteUser;
import bot.demo.master.api.live.LiveProcess;
import bot.demo.master.api.live.LiveUser;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import machine.lib.message.api.NetworkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class MasterServiceImpl implements MasterService, AutoCloseable {

    private static final int SCAN_RATE = 10;
    private static final Logger LOG = LoggerFactory.getLogger(MasterServiceImpl.class);
    private final ScheduledExecutorService scheduler;
    private final Cache<UUID, LiveProcess> processCache;
    private final Cache<String, LiveUser> userCache;
    private final Function<UUID, RemoteProcess> remoteProcessFunction;
    private final Function<String, RemoteUser> remoteUserFunction;
    private final ConsumerService remoteConsumer;
    private final NetworkConnector networkConnector;

    public MasterServiceImpl(NetworkConnector networkConnector) {
        this.networkConnector = networkConnector;
        this.remoteConsumer = this.networkConnector.remoteService(ConsumerService.class);
        this.remoteUserFunction = this.networkConnector.remoteObject(RemoteUser.class);
        this.remoteProcessFunction = this.networkConnector.remoteObject(RemoteProcess.class);
        this.processCache = CacheBuilder.newBuilder().expireAfterAccess(SCAN_RATE * 2, TimeUnit.SECONDS).build();
        this.userCache = CacheBuilder.newBuilder().expireAfterAccess(SCAN_RATE * 2, TimeUnit.SECONDS).build();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Publishes this {@link MasterService} implementation on the network, initiates scanning.
     */
    public void start() {
        networkConnector.listen(MasterService.class, this);
        scheduler.scheduleAtFixedRate(remoteConsumer::notifyScan, 0, SCAN_RATE, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(() -> LOG.info("Stats - Processes: {}, Users: {}", processCache.size(), userCache.size()), 1, SCAN_RATE, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::distributeTasks, 3, SCAN_RATE, TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        networkConnector.stopListen(MasterService.class, this);
    }

    public void distributeTasks(){
        processCache.asMap().forEach((UUID id, LiveProcess process) -> {
            if(process.getSlots() > 0){
                String randomUsername = UUID.randomUUID().toString();
                String randomPassword = UUID.randomUUID().toString();
                process.getRemoteProcess().doLogin(randomUsername, randomPassword);
            }
        });
        userCache.asMap().forEach((String username, LiveUser user) -> {
            RemoteUser remoteUser = user.getRemoteUser();
            remoteUser.doChat("world", "hello");
        });
    }

    @Override
    public void completedLogin(UUID processId, String username) {
        try {
            LOG.debug("completed login, processId = {}, username = {}", processId, username);
            userCache.get(username, () -> {
                RemoteUser remoteUser = remoteUserFunction.apply(username);
                return new LiveUser(username, remoteUser);
            });
        } catch (ExecutionException e) {
            LOG.error("Erred while creating a RemoteUser object", e);
        }
    }

    @Override
    public void completedLogout(UUID processId, String username) {
        LOG.debug("completed logout, processId = {}, username = {}", processId, username);
        userCache.invalidate(username);
    }

    @Override
    public void completedRegister(UUID processId, String username, String password) {
        try {
            LOG.debug("completed register, processId = {}, username = {}, password = {}", processId, username, password);
            userCache.get(username, () -> {
                RemoteUser remoteUser = remoteUserFunction.apply(username);
                return new LiveUser(username, remoteUser);
            });
        } catch (ExecutionException e) {
            LOG.error("Erred while creating a RemoteUser object", e);
        }
    }

    @Override
    public void completedScan(UUID processId, Integer slots, Collection<String> usernames) {
        try {
            LOG.debug("completed process scan, processId = {}, slots = {}, usernames = {}", processId, slots, usernames);
            processCache.get(processId, () -> {
                RemoteProcess remoteUser = remoteProcessFunction.apply(processId);
                return new LiveProcess(processId, remoteUser, slots);
            });
            for (String username : usernames) {
                userCache.get(username, () -> {
                    RemoteUser remoteUser = remoteUserFunction.apply(username);
                    return new LiveUser(username, remoteUser);
                });
            }
        } catch (ExecutionException e) {
            LOG.error("Erred while creating a RemoteProcess object", e);
        }
    }

}
