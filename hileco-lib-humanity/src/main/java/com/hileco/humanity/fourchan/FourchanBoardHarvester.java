package com.hileco.humanity.fourchan;

import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class FourchanBoardHarvester {

    private static final Logger LOG = LoggerFactory.getLogger(FourchanBoardHarvester.class);

    private final ExecutorService executorService;
    private final FourchanHttpClient fourchanHttpClient;

    public FourchanBoardHarvester() {
        this(5);
    }

    /**
     *
     * @param maxParallel maximum amount of threads to request in parallel
     */
    public FourchanBoardHarvester(int maxParallel) {
        this.executorService = Executors.newFixedThreadPool(maxParallel);
        this.fourchanHttpClient = new FourchanHttpClient();
    }

    /**
     * Loads post comments of threads of a given board into the given trainable.
     *
     * @param trainable trainable to receive post comments
     * @param board     the board to load from
     */
    public void harvestBoard(Consumer<String> trainable, String board) {
        try {
            String path = String.format("%s/catalog.json", board);
            JsonNode response = this.fourchanHttpClient.get(path);
            Iterator<JsonNode> pageIterator = response.getElements();
            while (pageIterator.hasNext()) {
                JsonNode page = pageIterator.next();
                Iterator<JsonNode> threadIterator = page.get("threads").getElements();
                while (threadIterator.hasNext()) {
                    JsonNode thread = threadIterator.next();
                    Integer threadNo = thread.get("no").getIntValue();
                    FourchanThreadHarvester fourchanThreadHarvester = new FourchanThreadHarvester(threadNo, board, trainable);
                    executorService.submit(fourchanThreadHarvester);
                }
            }
        } catch (IOException e) {
            LOG.error("unable to load board: {}.", board, e);
        }
    }

    /**
     * Cancels all scheduled thread requests by shutting down the executor service.
     *
     * Delegates to {@link java.util.concurrent.ExecutorService#isShutdown()}
     */
    public void shutdown() {
        executorService.shutdown();
    }

    /**
     * Delegates to {@link java.util.concurrent.ExecutorService#awaitTermination(long, java.util.concurrent.TimeUnit)}
     */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executorService.awaitTermination(timeout, unit);
    }

}
