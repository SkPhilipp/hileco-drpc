package machine.humanity.harvesting.fourchan;

import machine.humanity.generating.Trainable;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Loads a thread into a trainable, implpements Runnable so it can be ran in a thread.
 */
public class FourchanThreadHarvester implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(FourchanThreadHarvester.class);

    private static final Pattern REMOVE_IMPLYIN = Pattern.compile("&gt;&gt;\\d+");
    private static final Pattern REMOVE_SPECIAL = Pattern.compile("&#?[\\w\\d]+;");
    private static final Pattern REMOVE_HTML = Pattern.compile("(<([^>]+)>)");
    private final FourchanHttpClient fourchanHttpClient;

    private final Trainable trainable;
    private final String board;
    private final Integer threadNo;

    /**
     * @param trainable trainable to receive post comments
     * @param board     the board to load from
     * @param threadNo  thread number
     */
    public FourchanThreadHarvester(Integer threadNo, String board, Trainable trainable) {
        this.fourchanHttpClient = new FourchanHttpClient();
        this.threadNo = threadNo;
        this.board = board;
        this.trainable = trainable;
    }

    /**
     * Loads post comments of a thread by the threadNo of a the board into the trainable.
     */
    public void run() {
        try {
            String path = String.format("%s/res/%d.json", board, threadNo);
            JsonNode response = this.fourchanHttpClient.get(path);
            Iterator<JsonNode> postIterator = response.get("posts").getElements();
            while (postIterator.hasNext()) {
                JsonNode post = postIterator.next();
                if (post.has("com")) {
                    String comment = post.get("com").asText();
                    comment = REMOVE_IMPLYIN.matcher(comment).replaceAll("");
                    comment = REMOVE_SPECIAL.matcher(comment).replaceAll("");
                    comment = REMOVE_HTML.matcher(comment).replaceAll("");
                    trainable.train(comment);
                }
            }
        } catch (IOException e) {
            LOG.warn("Unable to load thread for board {}, thread number {}.", board, threadNo, e);
        }
    }

}
