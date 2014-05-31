package machine.router.dao;

import machine.router.api.entities.Subscription;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Example;
import org.hibernate.service.ServiceRegistry;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Most likely not fully functional, but for short testing, this works.
 */
public class SubscriptionDAO {

    private static final SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public Session openSession() {
        return sessionFactory.openSession();
    }

    public Subscription save(Subscription instance) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        if (instance.getId() == null) {
            UUID id = UUID.randomUUID();
            instance.setId(id);
            session.save(instance);
        } else {
            session.update(instance);
        }
        transaction.commit();
        session.close();
        return instance;
    }

    @SuppressWarnings("unchecked")
    public Subscription read(UUID id) {
        Session session = sessionFactory.openSession();
        Subscription result = (Subscription) session.get(Subscription.class, id);
        session.close();
        return result;
    }

    @SuppressWarnings("unchecked")
    public void delete(UUID id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Object deleteable = session.get(Subscription.class, id);
        if (deleteable != null) {
            session.delete(deleteable);
        }
        transaction.commit();
        session.close();
    }

    @SuppressWarnings("unchecked")
    public List<Subscription> query(Subscription example) {
        Session session = this.openSession();
        try {
            return session.createCriteria(Subscription.class).add(Example.create(example)).list();
        } finally {
            session.close();
        }
    }

    @SuppressWarnings("unchecked")
    public <QT> QT query(Function<Criteria, QT> queryModifier) {
        Session session = this.openSession();
        try {
            Criteria criteria = session.createCriteria(Subscription.class);
            return queryModifier.apply(criteria);
        } finally {
            session.close();
        }
    }

}
