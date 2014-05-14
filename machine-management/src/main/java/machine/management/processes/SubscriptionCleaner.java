package machine.management.processes;

import machine.management.api.entities.Subscription;
import machine.management.dao.GenericModelDAO;
import machine.management.dao.QueryModifier;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Polls all expired subscriptions, and deletes them.
 */
public class SubscriptionCleaner implements Runnable {

    private static final GenericModelDAO<Subscription> subscriptionDAO = new GenericModelDAO<>(Subscription.class);

    @Override
    public void run() {
        List<Subscription> expiredSubscriptions = subscriptionDAO.query(new QueryModifier<List<Subscription>>() {
            @Override
            @SuppressWarnings("unchecked")
            public List<Subscription> call(Criteria criteria) {
                Date now = Calendar.getInstance().getTime();
                criteria.add(Restrictions.lt(Subscription.EXPIRES, now));
                return criteria.list();
            }
        });
        for(Subscription subscription : expiredSubscriptions){
            subscriptionDAO.delete(subscription.getId());
        }
    }

}
