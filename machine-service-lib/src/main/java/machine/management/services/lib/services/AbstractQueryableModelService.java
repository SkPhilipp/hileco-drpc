package machine.management.services.lib.services;

import machine.management.services.lib.model.Model;
import org.hibernate.Session;
import org.hibernate.criterion.Example;

import java.util.List;

/**
 * Complete implementation of {@link QueryableModelService}
 *
 * @param <T> any persistable and identifyable entity model.
 */
public abstract class AbstractQueryableModelService<T extends Model> extends AbstractModelService<T> implements QueryableModelService<T> {

    @SuppressWarnings("unchecked")
    @Override
    public List<T> query(T example) {
        Session session = this.openSession();
        try {
            Class<? super T> type = this.getType();
            return session.createCriteria(type).add(Example.create(example)).list();
        } finally {
            session.close();
        }
    }

}
