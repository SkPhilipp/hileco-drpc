package machine.management.services.lib.dao;

import com.google.common.base.Preconditions;
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
    public UUID create(T instance) {
        UUID id = UUID.randomUUID();
        instance.setId(id);
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.save(instance);
        transaction.commit();
        session.close();
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T read(UUID id) {
        Session session = sessionFactory.openSession();
        try {
            return (T) session.get(type, id);
        } finally {
            session.close();
        }
    }

    @Override
    public void update(T instance) {
        Preconditions.checkArgument(instance.getId() != null, "Instance ID must be given, cannot find entity to update without it.");
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.update(instance);
        transaction.commit();
        session.close();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void delete(UUID id) {
        try {
            T instance = (T) type.newInstance();
            instance.setId(id);
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            session.delete(instance);
            transaction.commit();
            session.close();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Unable to instantiate an instance to assign an id to.");
        }
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
