package machine.humanity.harvesting.fourchan;

import machine.humanity.generating.ngram.NGramSentenceGenerator;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

@Ignore
public class FourchanThreadHarvesterTest {

    @Test
    public void testRun() throws Exception {

        NGramSentenceGenerator nGramSentenceGenerator = new NGramSentenceGenerator(3);
        FourchanBoardHarvester boardHarvester = new FourchanBoardHarvester();
        boardHarvester.harvestBoard(nGramSentenceGenerator, "b");
        boardHarvester.shutdown();
        boardHarvester.awaitTermination(10, TimeUnit.MINUTES);

        for (int i = 0; i < 100; i++) {
            String generated = nGramSentenceGenerator.generate();
            System.out.println(generated);
        }

    }

}
