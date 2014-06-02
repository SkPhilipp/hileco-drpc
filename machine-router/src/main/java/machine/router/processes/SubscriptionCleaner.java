package machine.router.processes;

import machine.router.dao.SubscriptionDAO;
import machine.drcp.core.api.entities.Subscription;
import org.hibernate.criterion.Restrictions;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Polls all expired subscriptions, and deletes them.
 */
public class SubscriptionCleaner implements Runnable {

    private static final SubscriptionDAO subscriptionDAO = new SubscriptionDAO();

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        List<Subscription> expiredSubscriptions = subscriptionDAO.query(criteria -> {
            Date now = Calendar.getInstance().getTime();
            criteria.add(Restrictions.lt(Subscription.EXPIRES, now));
            return criteria.list();
        });
        for(Subscription subscription : expiredSubscriptions){
            subscriptionDAO.delete(subscription.getId());
        }
    }

}
