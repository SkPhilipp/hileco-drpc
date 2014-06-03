package bot.demo.master.humanity;

import machine.humanity.generating.ngram.NGramSentenceGenerator;
import machine.humanity.harvesting.fourchan.HarvesterStatus;
import machine.humanity.harvesting.fourchan.FourchanBoardHarvester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FourchanHarvester {

    private static final Logger LOG = LoggerFactory.getLogger(FourchanHarvester.class);
    private static final Map<String, NGramSentenceGenerator> trainableGeneratorMap = new HashMap<>();
    private static final Map<String, HarvesterStatus> generatorStatusMap = new HashMap<>();

    public HarvesterStatus status(String source) {
        HarvesterStatus harvesterStatus = generatorStatusMap.get(source);
        return harvesterStatus == null ? HarvesterStatus.NONE : harvesterStatus;
    }

    /**
     * @param source a 4chan board name
     * @return harvesting status
     */
    public HarvesterStatus harvest(final String source) {
        HarvesterStatus harvesterStatus = this.status(source);
        if (harvesterStatus == HarvesterStatus.NONE) {
            generatorStatusMap.put(source, HarvesterStatus.HARVESTING);
            // create the new generator
            NGramSentenceGenerator gramSentenceGenerator = new NGramSentenceGenerator(4);
            trainableGeneratorMap.put(source, gramSentenceGenerator);
            // initiate the harvester on the trainable generator
            new Thread(() -> {
                try {
                    LOG.info("Harvesting:" + source);
                    FourchanBoardHarvester boardHarvester = new FourchanBoardHarvester(10);
                    boardHarvester.harvestBoard(gramSentenceGenerator, source);
                    boardHarvester.shutdown();
                    boardHarvester.awaitTermination(100, TimeUnit.DAYS);
                    generatorStatusMap.put(source, HarvesterStatus.HARVESTED);
                } catch (InterruptedException e) {
                    LOG.warn("Erred while awaiting termination of board harvester.", e);
                    generatorStatusMap.put(source, HarvesterStatus.ERRED);
                }
            }).start();
        }
        return this.status(source);
    }

    public List<String> generate(String source, Integer amount) {
        if (this.status(source) == HarvesterStatus.HARVESTED) {
            List<String> stringList = new ArrayList<>();
            NGramSentenceGenerator trainableGenerator = trainableGeneratorMap.get(source);
            for (int i = 0; i < Math.min(amount, 100); i++) {
                String generated = trainableGenerator.get();
                stringList.add(generated);
            }
            return stringList;
        } else {
            throw new IllegalStateException("Status for source must equal HARVESTED");
        }
    }

}
