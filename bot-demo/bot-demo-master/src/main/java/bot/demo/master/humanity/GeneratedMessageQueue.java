package bot.demo.master.humanity;

import com.google.common.collect.Queues;
import machine.humanity.api.domain.HarvesterStatus;
import machine.humanity.api.services.GeneratorService;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Generates text in batches and makes the generated text available through a queue.
 */
public class GeneratedMessageQueue implements AutoCloseable {

    public static final int MESSAGE_QUEUE_REFILL_LIMIT = 50;
    public static final int MESSAGE_QUEUE_REFILL_SIZE = 100;
    private final ScheduledExecutorService scheduler;
    private final GeneratorService generatorService;
    private final String generatorSource;
    private final Queue<String> messageQueue;

    public GeneratedMessageQueue(GeneratorService generatorService, String generatorSource) {
        this.generatorService = generatorService;
        this.generatorSource = generatorSource;
        this.messageQueue = Queues.newConcurrentLinkedQueue();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::fillMessageQueue, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        scheduler.shutdown();
    }

    public void fillMessageQueue() {
        if (messageQueue.size() < MESSAGE_QUEUE_REFILL_LIMIT) {
            if (generatorService.status(generatorSource).equals(HarvesterStatus.HARVESTED)) {
                List<String> generated = generatorService.generate(generatorSource, MESSAGE_QUEUE_REFILL_SIZE);
                messageQueue.addAll(generated);
            }
        }
    }

    public String poll() {
        return messageQueue.poll();
    }

    public String peek() {
        return messageQueue.peek();
    }

}
