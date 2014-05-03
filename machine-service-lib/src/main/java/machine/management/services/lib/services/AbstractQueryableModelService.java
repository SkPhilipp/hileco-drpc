package machine.management.services.lib.services;

import machine.management.services.lib.dao.GenericModelDAO;
import machine.management.services.lib.dao.Model;

import java.util.List;

/**
 * Complete implementation of {@link QueryableModelService} using {@link machine.management.services.lib.dao.GenericModelDAO}
 *
 * @param <T> any persistable and identifyable entity model.
 */
public abstract class AbstractQueryableModelService<T extends Model> extends AbstractModelService<T> implements QueryableModelService<T> {

    public AbstractQueryableModelService(GenericModelDAO<T> modelDAO) {
        super(modelDAO);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> query(T example) {
        return getModelDAO().query(example);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> query(T example, int offset, int limit) {
        return getModelDAO().query(example, offset, limit);
    }

    @Override
    public long count(T example) {
        return getModelDAO().count(example);
    }

}
