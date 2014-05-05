package machine.humanity.services;

import machine.humanity.api.domain.HarvesterStatus;
import machine.humanity.api.services.GeneratorService;
import machine.humanity.generating.TrainableGenerator;
import machine.humanity.generating.ngram.NGramSentenceGenerator;
import machine.humanity.harvesting.fourchan.FourchanBoardHarvester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of {@link GeneratorService} using 4chan as its sources.
 */
public class GeneratorServiceImpl implements GeneratorService {

    private static final Logger LOG = LoggerFactory.getLogger(GeneratorServiceImpl.class);
    private static final Map<String, TrainableGenerator> trainableGeneratorMap = new HashMap<>();
    private static final Map<String, HarvesterStatus> generatorStatusMap = new HashMap<>();

    @Override
    public HarvesterStatus status(String source) {
        HarvesterStatus harvesterStatus = generatorStatusMap.get(source);
        return harvesterStatus == null ? HarvesterStatus.NONE : harvesterStatus;
    }

    @Override
    public HarvesterStatus harvest(final String source) {
        HarvesterStatus harvesterStatus = this.status(source);
        if (harvesterStatus == HarvesterStatus.NONE) {
            this.generatorStatusMap.put(source, HarvesterStatus.HARVESTING);
            // create the new generator
            final TrainableGenerator gramSentenceGenerator = new NGramSentenceGenerator(4);
            trainableGeneratorMap.put(source, gramSentenceGenerator);
            // initiate the harvester on the trainable generator
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        LOG.info("Harvesting:" + source);
                        FourchanBoardHarvester boardHarvester = new FourchanBoardHarvester(10);
                        boardHarvester.harvestBoard(gramSentenceGenerator, source);
                        boardHarvester.shutdown();
                        boardHarvester.awaitTermination(100, TimeUnit.DAYS);
                        GeneratorServiceImpl.this.generatorStatusMap.put(source, HarvesterStatus.HARVESTED);
                    } catch (InterruptedException e) {
                        LOG.warn("Erred while awaiting termination of board harvester.", e);
                        GeneratorServiceImpl.this.generatorStatusMap.put(source, HarvesterStatus.ERRED);
                    }
                }
            }).start();
        }
        return this.status(source);
    }

    @Override
    public List<String> generate(String source, Integer amount) {
        if (this.status(source) == HarvesterStatus.HARVESTED) {
            List<String> stringList = new ArrayList<>();
            TrainableGenerator trainableGenerator = this.trainableGeneratorMap.get(source);
            for (int i = 0; i < Math.min(amount, 100); i++) {
                String generated = trainableGenerator.generate();
                stringList.add(generated);
            }
            return stringList;
        } else {
            throw new IllegalStateException("Status for source must equal HARVESTED");
        }
    }

}
