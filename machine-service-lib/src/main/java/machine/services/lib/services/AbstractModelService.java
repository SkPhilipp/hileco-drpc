package machine.services.lib.services;

import com.google.common.reflect.TypeToken;
import machine.services.lib.model.Model;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.UUID;

/**
 * Complete implementation of {@link ModelService}
 *
 * @param <T> any persistable and identifyable entity model.
 */
public abstract class AbstractModelService<T extends Model> implements ModelService<T> {

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

    private final Class<? super T> type;

    public AbstractModelService() {
        TypeToken<T> typeToken = new TypeToken<T>(getClass()) {
        };
        this.type = typeToken.getRawType();
    }

    public Session openSession() {
        return sessionFactory.openSession();
    }

    public Class<? super T> getType() {
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

}
