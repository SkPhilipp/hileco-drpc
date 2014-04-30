package machine.management.services.generic;

import com.google.common.reflect.TypeToken;
import machine.management.model.Model;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Example;
import org.hibernate.service.ServiceRegistry;

import java.util.List;
import java.util.UUID;

/**
 * Complete implementation of {@link ModelService}
 *
 * @param <T> any persistable entity model with operations matching the methods available on this class.
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
        TypeToken<T> typeToken = new TypeToken<T>(getClass()) { };
        this.type = typeToken.getRawType();
    }

    public Session openSession() {
        return sessionFactory.openSession();
    }

    @Override
    public UUID create(T instance) {
        UUID id = UUID.randomUUID();
        instance.setId(id);
        Session session = sessionFactory.openSession();
        session.save(instance);
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
    public void update(UUID id, T instance) {
        instance.setId(id);
        Session session = sessionFactory.openSession();
        session.update(instance);
        session.close();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void delete( UUID id) {
        try {
            T instance = (T) type.newInstance();
            instance.setId(id);
            Session session = sessionFactory.openSession();
            session.delete(instance);
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
            return session.createCriteria(type).add(Example.create(example)).list();
        } finally {
            session.close();
        }
    }

}
