package bot.demo.master.api;

import bot.demo.consumer.live.GlobalConsumer;
import bot.demo.consumer.live.LiveProcess;
import bot.demo.consumer.live.LiveUser;
import bot.demo.consumer.live.descriptors.ProcessDescriptor;
import bot.demo.master.api.live.RemoteLiveProcess;
import bot.demo.master.api.live.RemoteLiveUser;
import bot.demo.master.humanity.FourchanHarvester;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import machine.drcp.core.api.Client;
import machine.drcp.core.api.Connector;
import machine.humanity.harvesting.fourchan.HarvesterStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MasterServiceImpl {

    private static final int SCAN_RATE = 100;
    private static final Logger LOG = LoggerFactory.getLogger(MasterServiceImpl.class);
    private final ScheduledExecutorService scheduler;
    private final Cache<UUID, RemoteLiveProcess> processCache;
    private final Cache<String, RemoteLiveUser> userCache;
    private final FourchanHarvester harvester;
    private Connector<GlobalConsumer, ?> globalConsumerConnector;
    private Connector<LiveProcess, UUID> processConnector;
    private Connector<LiveUser, String> userConnector;

    public MasterServiceImpl(Client client, String harvestSource) {
        this.processCache = CacheBuilder.newBuilder().expireAfterAccess(SCAN_RATE * 2, TimeUnit.MILLISECONDS).build();
        this.userCache = CacheBuilder.newBuilder().expireAfterAccess(SCAN_RATE * 2, TimeUnit.MILLISECONDS).build();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.globalConsumerConnector = client.connector(GlobalConsumer.class);
        this.processConnector = client.connector(LiveProcess.class);
        this.userConnector = client.connector(LiveUser.class);
        this.harvester = new FourchanHarvester(harvestSource);
    }

    public void start() {
        this.harvester.harvest();
        this.scheduler.scheduleAtFixedRate(() -> this.globalConsumerConnector.drpc(GlobalConsumer::scan, this::completedScan), 0, SCAN_RATE, TimeUnit.MILLISECONDS);
        this.scheduler.scheduleAtFixedRate(() -> LOG.info("Stats - Processes: {}, Users: {}", processCache.size(), userCache.size()), 1, SCAN_RATE, TimeUnit.MILLISECONDS);
        this.scheduler.scheduleAtFixedRate(this::distributeTasks, 2, SCAN_RATE, TimeUnit.MILLISECONDS);
    }

    public void distributeTasks() {
        processCache.asMap().values().parallelStream().forEach(process -> {
            if (process.getSlots() > 0) {
                String username = UUID.randomUUID().toString();
                String password = UUID.randomUUID().toString();
                boolean result = process.getLive().login(username, password);
                if (result) {
                    try {
                        userCache.get(username, () -> new RemoteLiveUser(username, userConnector.connect(username)));
                    } catch (ExecutionException ignored) {
                    }
                }
            }
        });
        userCache.asMap().values().parallelStream().forEach(user -> {
            if (this.harvester.status() == HarvesterStatus.HARVESTED) {
                user.getLive().chat("world", this.harvester.generate());
            }
        });
    }

    public void completedScan(ProcessDescriptor processDescriptor) {
        UUID processId = processDescriptor.getProcessId();
        Integer slots = processDescriptor.getSlots();
        Collection<String> usernames = processDescriptor.getUsernames();
        try {
            LOG.debug("completed process scan, processId = {}, slots = {}, usernames = {}", processId, slots, usernames);
            processCache.get(processId, () -> new RemoteLiveProcess(processId, processConnector.connect(processId), slots));
            for (String username : usernames) {
                userCache.get(username, () -> new RemoteLiveUser(username, userConnector.connect(username)));
            }
        } catch (ExecutionException e) {
            LOG.error("Erred while creating a RemoteProcess object", e);
        }
    }

}
