package machine.management.dao;

import machine.management.api.entities.Model;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;
import org.hibernate.service.ServiceRegistry;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Complete implementation of {@link ModelDAO}
 *
 * @param <T> any persistable and identifyable entity model.
 */
public class GenericModelDAO<T extends Model> implements ModelDAO<T> {

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

    private final Class<T> type;

    public GenericModelDAO(Class<T> type) {
        this.type = type;
    }

    /**
     * Open a {@link Session}.
     *
     * @return The created session.
     */
    public Session openSession() {
        return sessionFactory.openSession();
    }

    public Class<T> getType() {
        return type;
    }

    @Override
    public T save(T instance) {
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
    @Override
    public T read(UUID id) {
        Session session = sessionFactory.openSession();
        T result = (T) session.get(type, id);
        session.close();
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void delete(UUID id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Object deleteable = session.get(type, id);
        if (deleteable != null) {
            session.delete(deleteable);
        }
        transaction.commit();
        session.close();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> query(T example) {
        Session session = this.openSession();
        try {
            Class<T> type = this.getType();
            return session.createCriteria(type).add(Example.create(example)).list();
        } finally {
            session.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <QT> QT query(Function<Criteria, QT> queryModifier) {
        Session session = this.openSession();
        try {
            Class<T> type = this.getType();
            Criteria criteria = session.createCriteria(type);
            return queryModifier.apply(criteria);
        } finally {
            session.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> query(T example, int offset, int limit) {
        Session session = this.openSession();
        try {
            Class<T> type = this.getType();
            return session.createCriteria(type).add(Example.create(example))
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .list();
        } finally {
            session.close();
        }
    }

    @Override
    public long count(T example) {
        Session session = this.openSession();
        try {
            Class<T> type = this.getType();
            Criteria criteria = session.createCriteria(type).add(Example.create(example));
            return (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
        } finally {
            session.close();
        }
    }

}
