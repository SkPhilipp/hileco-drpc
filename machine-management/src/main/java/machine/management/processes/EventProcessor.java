package machine.management.processes;

import machine.management.domain.Event;
import machine.management.domain.Message;
import machine.management.processes.events.EventRouter;
import machine.management.services.lib.dao.GenericModelDAO;
import machine.management.services.lib.dao.QueryModifier;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A task which polls for events and routes them out.
 */
public class EventProcessor extends TimerTask implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(EventProcessor.class);
    private static final Integer BATCH_SIZE = 50;
    private static final Integer AUTOMATIC_POLL_INTERVAL = 1000;
    private static EventProcessor INSTANCE;

    private EventRouter eventRouter;
    private GenericModelDAO<Message> messageDAO;
    private GenericModelDAO<Event> eventDAO;
    private long lastTimestamp;

    public EventProcessor() {
        this.eventRouter = new EventRouter();
        this.messageDAO = new GenericModelDAO<>(Message.class);
        this.eventDAO = new GenericModelDAO<>(Event.class);
        this.lastTimestamp = 0L;
    }

    public static EventProcessor getInstance() {
        if (INSTANCE == null) {
            // instantiate and schedule to run
            INSTANCE = new EventProcessor();
            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(INSTANCE, 0, AUTOMATIC_POLL_INTERVAL);
        }
        return INSTANCE;
    }

    /**
     * Polls for {@link #BATCH_SIZE} queries with a timestamp greater than or equalling sinceTimestamp.
     *
     * @param sinceTimestamp timestamp to filter by
     * @param limit          batch size limit
     * @return the set of events
     */
    public List<Event> pollSince(final long sinceTimestamp, int limit) {
        return eventDAO.query(new QueryModifier<List<Event>>() {
            @Override
            @SuppressWarnings("unchecked")
            public List<Event> call(Criteria criteria) {
                criteria.add(Restrictions.ge("timestamp", sinceTimestamp));
                return (List<Event>) criteria.list();
            }
        }, 0, limit);
    }

    synchronized private void poll(int batchSize){
        LOG.debug("Polling for events");
        List<Event> events = this.pollSince(lastTimestamp, batchSize);
        for (Event event : events) {
            if (event.getTimestamp() > this.lastTimestamp) {
                this.lastTimestamp = event.getTimestamp();
            }
            Message message = this.messageDAO.read(event.getId());
            LOG.trace("Submitting event with id {}", event.getId());
            eventRouter.submit(event, message);
            eventDAO.delete(event.getId());
        }
    }

    /**
     * Alerts the message routing process that a new message is available to poll from the database.
     */
    public void alert() {
        this.poll(1);
    }

    @Override
    public void run() {
        this.poll(BATCH_SIZE);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOG.debug("Initializing event router");
        // make sure an instance is available
        EventProcessor.getInstance();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.debug("Shutting down event router");
        this.eventRouter.shutdown();
    }

}
