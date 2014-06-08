package bot.demo.master.humanity;

import machine.humanity.generating.ngram.NGramSentenceGenerator;
import machine.humanity.harvesting.fourchan.FourchanBoardHarvester;
import machine.humanity.harvesting.fourchan.HarvesterStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class FourchanHarvester {

    private static final Logger LOG = LoggerFactory.getLogger(FourchanHarvester.class);
    private final NGramSentenceGenerator generator;
    private HarvesterStatus status;
    private String source;

    public FourchanHarvester(String source) {
        this.source = source;
        this.generator = new NGramSentenceGenerator(4);
        this.status = HarvesterStatus.NONE;
    }

    public HarvesterStatus status() {
        return this.status;
    }

    /**
     * @return harvesting status
     */
    public HarvesterStatus harvest() {
        if (this.status == HarvesterStatus.NONE) {
            status = HarvesterStatus.HARVESTING;
            // initiate the harvester on the trainable generator
            new Thread(() -> {
                try {
                    LOG.info("Harvesting:" + source);
                    FourchanBoardHarvester boardHarvester = new FourchanBoardHarvester(10);
                    boardHarvester.harvestBoard(generator, source);
                    boardHarvester.shutdown();
                    boardHarvester.awaitTermination(100, TimeUnit.DAYS);
                    status = HarvesterStatus.HARVESTED;
                } catch (InterruptedException e) {
                    LOG.warn("Erred while awaiting termination of board harvester.", e);
                    status = HarvesterStatus.ERRED;
                }
            }).start();
        }
        return this.status;
    }

    public String generate() {
        if (this.status == HarvesterStatus.HARVESTED) {
            return generator.get();
        } else {
            throw new IllegalStateException("Source is not harvested");
        }
    }

}
